package com.moco.moco.controller;

import static com.moco.moco.common.ResponseEntityConstants.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.config.argsResolver.CurrentLoginUser;
import com.moco.moco.config.argsResolver.UserInfo;
import com.moco.moco.dto.PostDto;
import com.moco.moco.dto.queryDslDto.PostVo;
import com.moco.moco.service.post.PostService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@Validated
public class PostController {
	private final PostService postService;

	private final String OFFSET = "0";
	private final String LIMIT = "9";
	private final String RECRUIT = "false";

	@GetMapping("/public/posts")
	public ResponseEntity<PostDto.Response> getPosts(
		@RequestParam(value = "offset", required = false, defaultValue = OFFSET) Integer offset,
		@RequestParam(value = "limit", required = false, defaultValue = LIMIT) @Max(value = 100) Integer limit,
		@RequestParam(value = "recruit", required = false, defaultValue = RECRUIT) String recruit,
		@RequestParam(value = "username", required = false) String username,
		@RequestParam(value = "type", required = false) String type,
		@RequestParam(value = "position", required = false) String position,
		@RequestParam(value = "mode", required = false) String mode,
		@RequestParam(value = "language", required = false) String language) {
		PostDto.Response postDto = postService.getPosts(offset, limit, recruit, username, type, position, mode,
			language);
		return ResponseEntity.status(HttpStatus.OK)
			.body(postDto);
	}

	@GetMapping("/private/posts")
	public ResponseEntity<PostDto.Response> getMyBookmarkPosts(
		@RequestParam(value = "offset", required = false, defaultValue = OFFSET) Integer offset,
		@RequestParam(value = "limit", required = false, defaultValue = LIMIT) @Max(100) Integer limit,
		@RequestParam(value = "recruit", required = false, defaultValue = RECRUIT) String recruit,
		@CurrentLoginUser UserInfo userInfo) {
		PostDto.Response postDto = postService.getMyBookmarkPosts(offset, limit, recruit, userInfo.getId());
		return ResponseEntity.status(HttpStatus.OK)
			.body(postDto);
	}

	@GetMapping("/public/posts/{postId}")
	public ResponseEntity<PostVo> getPost(@PathVariable Long postId) {
		PostVo postDto = postService.getPost(postId);
		return ResponseEntity.status(HttpStatus.OK).body(postDto);
	}

	@GetMapping("/public/posts/near-deadline")
	public ResponseEntity<List<PostDto.PostList>> getPostsNearDeadline() {
		List<PostDto.PostList> postsNearDeadline = postService.getPostsNearDeadline();
		return ResponseEntity.ok().body(postsNearDeadline);
	}

	@PostMapping("/private/posts")
	public ResponseEntity<Long> createPost(
		@Valid @RequestBody PostDto.Request postDto,
		@CurrentLoginUser UserInfo userInfo) {
		Long savedPostId = postService.savePost(postDto, userInfo.getId());
		return ResponseEntity.status(HttpStatus.CREATED).body(savedPostId);
	}

	@PutMapping("/private/posts/{postId}")
	public ResponseEntity<Long> updatePost(
		@PathVariable(value = "postId") Long postId,
		@Valid @RequestBody PostDto.Request postDto,
		@CurrentLoginUser UserInfo userInfo) {
		Long savedPostId = postService.updatePost(postId, postDto, userInfo.getId());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(savedPostId);
	}

	@DeleteMapping("/private/posts/{postId}")
	public ResponseEntity<Long> removePost(@PathVariable("postId") Long postId,
		@CurrentLoginUser UserInfo userInfo) {
		Long removedPostId = postService.removePost(userInfo.getId(), postId);
		return ResponseEntity.status(HttpStatus.OK).body(removedPostId);
	}

	@PatchMapping("/private/posts/{postId}")
	public ResponseEntity<HttpStatus> closeRecruitment(@PathVariable("postId") Long postId,
		@CurrentLoginUser UserInfo userInfo) {
		postService.closeRecruitment(postId, userInfo.getId());
		return RESPONSE_ENTITY_OK;
	}
}










