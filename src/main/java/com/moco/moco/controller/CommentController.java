package com.moco.moco.controller;

import static com.moco.moco.common.ResponseEntityConstants.*;

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

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class CommentController {
	private final CommentService commentService;

	@GetMapping("/public/comments/{postId}")
	public ResponseEntity<List<CommentDto.Response>> getComments(@PathVariable Long postId) {
		List<CommentDto.Response> commentDto = commentService.getComments(postId);
		return ResponseEntity.status(HttpStatus.OK).body(commentDto);
	}

	@GetMapping("/public/comments-count/{postId}")
	public ResponseEntity<CommentDto.Count> getCommentsCount(@PathVariable Long postId) {
		CommentDto.Count commentsCountDto = commentService.getCommentsCount(postId);
		return ResponseEntity.status(HttpStatus.OK).body(commentsCountDto);
	}

	@PostMapping("/private/comments/{postId}")
	public ResponseEntity<CommentDto.Response> createComment(@PathVariable Long postId,
		@Valid @RequestBody CommentDto.Request request,
		@CurrentLoginUser UserInfo userInfo) {
		CommentDto.Response commentDto = commentService.createComment(userInfo.getId(), postId, request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(commentDto);
	}

	@PutMapping("/private/comments/{commentId}")
	public ResponseEntity<CommentDto.Response> commentUpdate(@PathVariable Long commentId,
		@Valid @RequestBody CommentDto.Request request,
		@CurrentLoginUser UserInfo userInfo) {
		CommentDto.Response commentDto = commentService.updateComment(userInfo.getId(), commentId, request);
		return ResponseEntity.status(HttpStatus.OK)
			.body(commentDto);
	}

	@DeleteMapping("/private/comments/{commentId}")
	public ResponseEntity<HttpStatus> deleteComment(@PathVariable Long commentId,
		@CurrentLoginUser UserInfo userInfo) {
		commentService.deleteComment(userInfo.getId(), commentId);
		return RESPONSE_ENTITY_NO_CONTENT;
	}
}
