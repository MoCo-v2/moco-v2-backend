package com.moco.moco.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.config.argsResolver.CurrentLoginUser;
import com.moco.moco.config.argsResolver.UserInfo;
import com.moco.moco.dto.CommentDto;
import com.moco.moco.service.post.CommentService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class CommentController {

	private final String OFFSET = "0";
	private final String LIMIT = "9";
	private final CommentService commentService;

	@GetMapping("/public/comments/{postId}")
	public ResponseEntity<List<CommentDto.Response>> getComments(@PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.OK).body(commentService.getComments(postId));
	}

	@PostMapping("/private/comments/{postId}")
	public ResponseEntity<CommentDto.Response> createComment(@PathVariable Long postId,
		@RequestBody CommentDto.Request commentDto,
		@CurrentLoginUser UserInfo userInfo) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(commentService.createComment(userInfo.getId(), postId, commentDto));
	}

	@PutMapping("/private/comments/{commentId}")
	public ResponseEntity<CommentDto.Response> commentUpdate(@PathVariable Long commentId,
		@RequestBody CommentDto.Request commentDto,
		@CurrentLoginUser UserInfo userInfo) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(commentService.updateComment(userInfo.getId(), commentId, commentDto));
	}

	@DeleteMapping("/private/comments/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
		@CurrentLoginUser UserInfo userInfo) {
		commentService.deleteComment(userInfo.getId(), commentId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
