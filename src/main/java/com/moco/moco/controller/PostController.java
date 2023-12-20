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

import com.moco.moco.config.argsResolver.LoginUserInfo;
import com.moco.moco.config.argsResolver.UserInfo;
import com.moco.moco.dto.PostDto;
import com.moco.moco.dto.queryDslDto.PostDetailVo;
import com.moco.moco.service.post.CommentService;
import com.moco.moco.service.post.PostService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class PostController {
	private final PostService postService;
	private final CommentService commentService;
	private final Logger log = LoggerFactory.getLogger(PostController.class);

	private final String OFFSET = "0";
	private final String LIMIT = "9";
	private final String RECRUIT = "false";

	@GetMapping("/public/posts")
	public ResponseEntity<PostDto.Response> getPosts(
		@RequestParam(value = "offset", required = false, defaultValue = OFFSET) Integer offset,
		@RequestParam(value = "limit", required = false, defaultValue = LIMIT) Integer limit,
		@RequestParam(value = "recruit", required = false, defaultValue = RECRUIT) String recruit,
		@RequestParam(value = "username", required = false) String username) {
		return ResponseEntity.ok().body(postService.getPosts(offset, limit, recruit, username));
	}

	@GetMapping("/public/posts/{postId}")
	public ResponseEntity<PostDetailVo> getPost(@PathVariable Long postId) {
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

	@DeleteMapping("/private/posts/{postId}")
	public ResponseEntity<Long> removePost(@PathVariable("postId") Long postId,
		@LoginUserInfo UserInfo userInfo) {
		return ResponseEntity.ok().body(postService.removePost(userInfo.getId(), postId));
	}
}










