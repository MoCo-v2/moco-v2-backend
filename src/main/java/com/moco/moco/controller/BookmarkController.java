package com.moco.moco.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.config.argsResolver.CurrentLoginUser;
import com.moco.moco.config.argsResolver.UserInfo;
import com.moco.moco.dto.BookmarkDto;
import com.moco.moco.service.post.BookmarkService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class BookmarkController {
	private final BookmarkService bookMarkService;

	@PostMapping("/private/bookmark/{postId}")
	public ResponseEntity<BookmarkDto.Response> createBookmark(@PathVariable Long postId,
		@CurrentLoginUser UserInfo userInfo) {
		return ResponseEntity.status(HttpStatus.CREATED).body(bookMarkService.createBookmark(userInfo.getId(), postId));
	}

	@DeleteMapping("/private/bookmark/{postId}")
	public ResponseEntity<Void> removeBookmark(@PathVariable Long postId,
		@CurrentLoginUser UserInfo userInfo) {
		bookMarkService.removeBookmark(userInfo.getId(), postId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
