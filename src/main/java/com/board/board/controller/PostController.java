package com.board.board.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.board.board.config.LoginUser;
import com.board.board.config.auth.SessionUser;
import com.board.board.dto.PostDto;
import com.board.board.dto.PostListVo;
import com.board.board.dto.RecruitDto;
import com.board.board.service.post.CommentService;
import com.board.board.service.post.LikeService;
import com.board.board.service.post.MarkDownService;
import com.board.board.service.post.PostService;
import com.board.board.service.post.RecruitService;
import com.board.board.service.util.Utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/* 게시판 */
@AllArgsConstructor
@RestController
@RequestMapping("posts")
public class PostController {
	private final PostService postService;
	private final CommentService commentService;
	private final LikeService likeService;
	private final RecruitService recruitService;
	private final MarkDownService markDownService;
	private final Utils utils;
	private final Logger log = LoggerFactory.getLogger(PostController.class);

	/* ----- Post 📋 ----- */
	@Operation(summary = "Return all posts data", description = "모든 게시글 데이터를 반환합니다.")
	@GetMapping("")
	public ResponseEntity<JSONObject> getBoards(
		@Parameter(description = "어디서 부터 가져올지 요청하는 파라미터입니다. 기본값은 0으로 첫번째 게시글부터 가져옵니다.")
		@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
		@Parameter(description = "어디까지 가져올지의 요청하는 파라미터입니다. 기본값은 8입니다.")
		@RequestParam(value = "limit", required = false, defaultValue = "8") Integer limit) {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("list", postService.getBoards(offset, limit));
		jsonObject.put("total", postService.getBoardCount());
		return ResponseEntity.ok().body(jsonObject);
	}

	@Operation(summary = "return post data", description = "특정 id를 가진 posts 데이터를 반환합니다.")
	@GetMapping("/{postId}")
	public ResponseEntity<PostDto.PostDetailDto> detail(
		@Parameter(description = "게시글 id") @PathVariable("postId") Long postId,
		@Parameter(description = "조회수 중복 방지를 위해 쿠키값을 가져오기 위한 파라미터입니다. ") HttpServletRequest request,
		@Parameter(description = "조회수 중복 방지를 위해 쿠키값을 가져오기 위한 파라미터입니다. ") HttpServletResponse response) {
		return ResponseEntity.ok().body(postService.getPost(postId, request, response));
	}

	/* READ - 무한스크롤 AJAX */
	@Operation(summary = "다음 페이지의 게시글들을 반환", description = "가져올 페이지번호를 받아 모집중인 게시글들을 반환합니다.")
	@GetMapping("/list-next/{page}/{isRecruitOn}")
	public ResponseEntity listJson(
		@Parameter(description = "가져올 게시글들의 페이지 번호입니다.") @PathVariable("page") Integer pageNum,
		@Parameter(description = "모집중인 게시글을 구분하기 위한 파라미터입니다.") @PathVariable("isRecruitOn") Boolean isRecruitOn) {
		List<PostListVo> boardList = new ArrayList<>();
		if (isRecruitOn) { /* 모집중만 */
			boardList = postService.getBoardListOnRecruit(pageNum);
		} else {           /* 전체 게시글 */
			//boardList = boardService.getBoards(pageNum);
		}
		return ResponseEntity.ok(boardList);
	}

	/* CREATE - 글작성 */
	@Operation(summary = "게시글 작성", description = "신규 게시글을 등록합니다.")
	@PostMapping("/write")
	public String write(@Parameter(description = "게시글의 정보가 담긴 Request 객체입니다.") @Valid PostDto.Request boardDto,
		Errors errors, @Parameter(description = "현재 로그인된 사용자를 식별") @LoginUser SessionUser sessionUser, Model model,
		@Parameter(description = "해시태그의 정보를 String 으로 받습니다. 후에 문자열 파싱을 통해 DB에 저장합니다.") @RequestParam(value = "tags", required = false) String tags) {
		/* 글작성 유효성 검사 */
		if (errors.hasErrors()) {
			/* 글작성 실패시 입력 데이터 값 유지 */
			model.addAttribute("boardDto", boardDto);
			/* 유효성 통과 못한 필드와 메세지를 핸들링 */
			model.addAttribute("error", "제목을 입력해주세요.");
			return "board/write";
		}

		/* 썸네일 부재시 디폴트값 설정 */
		if (boardDto.getThumbnail().equals("") || boardDto.getThumbnail().equals(null)) {
			boardDto.setThumbnail("/img/thumbnail.png");
		}
		boardDto.setWriter(sessionUser.getName());

		/* 해시태그 저장 */
		if (!tags.isEmpty()) {
			String tag = utils.hashtagParse(tags);
			boardDto.setHashtag(tag);
		}

		postService.savePost(sessionUser.getName(), boardDto);

		return "redirect:/board/list";
	}

	/* UPDATE - 게시글 수정 */
	@Operation(summary = "게시글 수정", description = "게시글을 수정 합니다. 수정 성공시 모집하기 페이지로 리다이렉트 됩니다.")
	@PutMapping("/edit/{boardId}")
	public String update(@Parameter(description = "해당 번호를 가진 게시글을 수정합니다.") @PathVariable("boardId") Long boardId,
		@Parameter(description = "수정된 게시글의 정보가 담긴 Request 객체 입니다.") @Valid PostDto.Request boardDto,
		@Parameter(description = "해시태그의 정보를 String 으로 받습니다. 후에 문자열 파싱을 통해 DB에 저장합니다.") @RequestParam(value = "tags", required = false) String tags,
		@LoginUser SessionUser sessionUser) {
		// if (!sessionUser.getId().equals(postService.getPost(boardId).getUserId())) {
		// 	return "error/404error";
		// }
		//
		// /* 해시태그 저장 */
		// if (!tags.isEmpty()) {
		// 	String tag = utils.hashtagParse(tags);
		// 	boardDto.setHashtag(tag);
		// }
		//
		// boardDto.setWriter(sessionUser.getName());
		// postService.updatePost(boardId, boardDto);
		//
		return "redirect:/board/list";
	}

	/* DELETE - 게시글 삭제 */
	@Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다. 삭제 성공시 모집하기 페이지로 리다이렉트 됩니다.")
	@DeleteMapping("/{boardId}")
	public String delete(@Parameter(description = "해당 번호를 가진 게시글을 삭제합니다.") @PathVariable("boardId") Long boardId,
		@LoginUser SessionUser sessionUser) {
		// if (!sessionUser.getId().equals(postService.getPost(boardId).getUserId())) {
		// 	return "/error/404error";
		// }
		//
		// postService.deletePost(boardId);
		return "redirect:/board/list";
	}

	/* READ - 검색 */
	@Operation(summary = "게시글 검색", description = "게시글을 검색합니다. 모집중인 게시글만 반환합니다.")
	@GetMapping("/search")
	public String search(
		@Parameter(description = "검색한 게시글들의 페이지 번호입니다. 기본값으로는 1 입니다.") @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
		@Parameter(description = "검색할 키워드가 담긴 파라미터입니다.") @RequestParam(value = "keyword") String keyword, Model model) {
		List<PostListVo> boardDtoList = postService.searchPosts(pageNum, keyword);
		model.addAttribute("boardList", boardDtoList);
		return "board/list";
	}

	/* CREATE - 스터디 참가 */
	@Operation(summary = "스터디 참가", description = "스터디에 참가합니다. 응답으로는 200 , 400 입니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "참가 성공의 경우 응답입니다."),
		@ApiResponse(responseCode = "400", description = "참가 실패의 경우 응답입니다."),
	})
	@PostMapping("/recruit/{boardId}/{userId}")
	public ResponseEntity recruitSave(@Parameter(description = "참가하는 게시글 번호입니다.") @PathVariable Long boardId,
		@Parameter(description = "참가하는 사용자의 번호입니다.") @PathVariable Long userId, @LoginUser SessionUser sessionUser) {
		if (!sessionUser.getId().equals(userId)) {
			return ResponseEntity.badRequest().build();
		}

		RecruitDto.Request dto = new RecruitDto.Request();

		boolean isDuplicate = recruitService.isDuplicate(boardId, userId);
		if (isDuplicate) {
			return ResponseEntity.badRequest().body("이미 신청하였습니다.");
		}
		return ResponseEntity.ok(recruitService.join(boardId, userId, dto));
	}

	/* DELETE - 모집 마감 취소 */
	@DeleteMapping("/recruit-cancel/{boardId}/{userId}")
	@Operation(summary = "모각코 모집 마감 취소", description = "모집 마감을 취소합니다. 게시글 작성자만 호출할수 있습니다.")
	public ResponseEntity recruitDelete(@Parameter(description = "모집 마감을 취소할 게시글의 번호입니다.") @PathVariable Long boardId,
		@Parameter(description = "모집 취소를 누른 사용자의 번호입니다.") @PathVariable Long userId,
		@LoginUser SessionUser sessionUser) {
		if (!sessionUser.getId().equals(userId)) {
			return ResponseEntity.badRequest().build();
		}

		int rows = recruitService.joinCancel(boardId, userId);
		int status = rows == 1 ? 200 : 400;
		return ResponseEntity.status(status).build();
	}

	/* UPDATE - 모집 마감 */
	@Operation(summary = "모각코 모집을 마감", description = "모각코 모집을 마감합니다. 게시글 작성자만 호출할수 있습니다.")
	@PatchMapping("/recruit-off/{boardId}")
	public ResponseEntity recruitClose(@Parameter(description = "해당 번호를 가진 게시글에 대해 요청합니다.") @PathVariable Long boardId,
		@LoginUser SessionUser sessionUser) {
		// if (!sessionUser.getId().equals(postService.getPost(boardId))) {
		// 	ResponseEntity.badRequest().build();
		// }
		return ResponseEntity.ok(postService.updateFull(boardId));
	}

}










