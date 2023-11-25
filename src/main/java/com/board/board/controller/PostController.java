package com.board.board.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
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
import com.board.board.dto.RecruitDto;
import com.board.board.service.post.PostService;
import com.board.board.service.post.RecruitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/* 게시판 */
@AllArgsConstructor
@RestController
@RequestMapping("posts")
public class PostController {
	private final PostService postService;
	private final RecruitService recruitService;
	private final Logger log = LoggerFactory.getLogger(PostController.class);

	private final String offset = "0";
	private final String limit = "8";

	/* ----- Post 📋 ----- */
	@Operation(summary = "Return posts", description = "특정 조건의 posts를 반환합니다.")
	@GetMapping("")
	public ResponseEntity<PostDto.Posts> getPosts(
		@Parameter(description = "어디서 부터 가져올지 요청하는 파라미터입니다. 기본값은 0으로 첫번째 게시글부터 가져옵니다.")
		@RequestParam(value = "offset", required = false, defaultValue = offset) Integer offset,
		@Parameter(description = "어디까지 가져올지의 요청하는 파라미터입니다. 기본값은 8입니다.")
		@RequestParam(value = "limit", required = false, defaultValue = limit) Integer limit) {

		return ResponseEntity.ok().body(postService.getPosts(offset, limit));
	}

	/* READ - 검색 */
	@Operation(summary = "게시글 검색", description = "게시글을 검색합니다. 모집중인 게시글만 반환합니다.")
	@GetMapping("/search")
	public ResponseEntity<PostDto.Posts> search(
		@Parameter(description = "어디서 부터 가져올지 요청하는 파라미터입니다. 기본값은 0으로 첫번째 게시글부터 가져옵니다.")
		@RequestParam(value = "offset", required = false, defaultValue = offset) Integer offset,
		@Parameter(description = "어디까지 가져올지의 요청하는 파라미터입니다. 기본값은 8입니다.")
		@RequestParam(value = "limit", required = false, defaultValue = limit) Integer limit,
		@Parameter(description = "검색할 키워드가 담긴 파라미터입니다.")
		@RequestParam(value = "keyword") String keyword, Model model) {
		return ResponseEntity.ok().body(postService.searchPosts(offset, limit, keyword));
	}

	@Operation(summary = "return post data", description = "특정 id를 가진 post를 반환합니다.")
	@GetMapping("/{postId}")
	public ResponseEntity<PostDto.PostDetailDto> detail(
		@Parameter(description = "게시글 id") @PathVariable("postId") Long postId) {
		return ResponseEntity.ok().body(postService.getPost(postId));
	}

	/* CREATE - 글작성 */
	@Operation(summary = "게시글 작성", description = "신규 게시글을 등록합니다.")
	@PostMapping("/{userId}")
	public ResponseEntity<Long> createPost(
		@Parameter(description = "게시글의 정보가 담긴 Request 객체입니다.") @Valid PostDto.Request boardDto,
		@Parameter(description = "게시글 작성을 요청한 유저 ID") @PathVariable("userId") Long userId) {
		return ResponseEntity.ok().body(postService.savePost(userId, boardDto));
	}

	/* UPDATE - 게시글 수정 */
	@Operation(summary = "게시글 수정", description = "게시글을 수정 합니다.")
	@PutMapping("/{postId}")
	public String update(@Parameter(description = "해당 번호를 가진 게시글을 수정합니다.") @PathVariable("postId") Long postId,
		@Parameter(description = "수정된 게시글의 정보가 담긴 Request 객체 입니다.") @Valid PostDto.Request postDto,
		@Parameter(description = "게시글 작성을 요청한 유저 ID") @PathVariable("userId") Long userId) {
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










