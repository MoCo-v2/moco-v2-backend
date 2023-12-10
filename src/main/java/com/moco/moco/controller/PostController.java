package com.moco.moco.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.config.LoginUserInfo;
import com.moco.moco.config.auth.UserInfo;
import com.moco.moco.dto.PostDto;
import com.moco.moco.dto.queryDslDto.PostDetailVo;
import com.moco.moco.service.post.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/* 게시판 */
@AllArgsConstructor
@RestController
public class PostController {
	private final PostService postService;
	private final Logger log = LoggerFactory.getLogger(PostController.class);

	private final String OFFSET = "0";
	private final String LIMIT = "9";

	@GetMapping("/public/posts")
	public ResponseEntity<PostDto.Response> getPostsOnRecruit(
		@RequestParam(value = "offset", required = false, defaultValue = OFFSET) Integer offset,
		@RequestParam(value = "limit", required = false, defaultValue = LIMIT) Integer limit) {
		return ResponseEntity.ok().body(postService.getPostsOnRecruit(offset, limit));
	}

	@GetMapping("/public/all-posts")
	public ResponseEntity<PostDto.Response> getPosts(
		@RequestParam(value = "offset", required = false, defaultValue = OFFSET) Integer offset,
		@RequestParam(value = "limit", required = false, defaultValue = LIMIT) Integer limit) {
		return ResponseEntity.ok().body(postService.getPosts(offset, limit));
	}

	@GetMapping("/public/posts/{postId}")
	public ResponseEntity<PostDetailVo> getPost(
		@PathVariable("postId") Long postId) {
		return ResponseEntity.ok().body(postService.getPost(postId));
	}

	@PostMapping("/private/posts")
	public ResponseEntity<Long> createPost(
		@Valid @RequestBody PostDto.Request postDto,
		@LoginUserInfo UserInfo userInfo) {
		return ResponseEntity.status(201).body(postService.savePost(postDto, userInfo.getId()));
	}

	@PutMapping("/private/posts/{postId}")
	public ResponseEntity<Long> updatePost(
		@PathVariable(value = "postId") Long postId,
		@Valid @RequestBody PostDto.Request postDto,
		@LoginUserInfo UserInfo userInfo) {
		return ResponseEntity.status(201).body(postService.updatePost(postId, postDto, userInfo.getId()));
	}

	/* DELETE - 게시글 삭제 */
	@Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다. 삭제 성공시 모집하기 페이지로 리다이렉트 됩니다.")
	@DeleteMapping("/{boardId}")
	public String delete(@Parameter(description = "해당 번호를 가진 게시글을 삭제합니다.") @PathVariable("boardId") Long boardId,
		@LoginUserInfo UserInfo sessionUser) {
		// if (!sessionUser.getId().equals(postService.getPost(boardId).getUserId())) {
		// 	return "/error/404error";
		// }
		//
		// postService.deletePost(boardId);
		return "redirect:/board/list";
	}

	/* CREATE - 스터디 참가 */
	// @Operation(summary = "스터디 참가", description = "스터디에 참가합니다. 응답으로는 200 , 400 입니다.")
	// @ApiResponses({
	// 	@ApiResponse(responseCode = "200", description = "참가 성공의 경우 응답입니다."),
	// 	@ApiResponse(responseCode = "400", description = "참가 실패의 경우 응답입니다."),
	// })
	// @PostMapping("/recruit/{boardId}/{userId}")
	// public ResponseEntity recruitSave(@Parameter(description = "참가하는 게시글 번호입니다.") @PathVariable Long boardId,
	// 	@Parameter(description = "참가하는 사용자의 번호입니다.") @PathVariable Long userId,
	// 	@LoginUserInfo UserInfo sessionUser) {
	// 	if (!sessionUser.getId().equals(userId)) {
	// 		return ResponseEntity.badRequest().build();
	// 	}
	//
	// 	RecruitDto.Request dto = new RecruitDto.Request();
	//
	// 	boolean isDuplicate = recruitService.isDuplicate(boardId, userId);
	// 	if (isDuplicate) {
	// 		return ResponseEntity.badRequest().body("이미 신청하였습니다.");
	// 	}
	// 	return ResponseEntity.ok(recruitService.join(boardId, userId, dto));
	// }
}










