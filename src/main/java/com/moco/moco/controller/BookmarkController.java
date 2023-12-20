package com.moco.moco.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.config.argsResolver.LoginUserInfo;
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
		@LoginUserInfo UserInfo userInfo) {
		return ResponseEntity.status(201).body(bookMarkService.createBookmark(userInfo.getId(), postId));
	}

	@DeleteMapping("/private/bookmark/{postId}")
	public ResponseEntity<BookmarkDto.Response> removeBookmark(@PathVariable Long postId,
		@LoginUserInfo UserInfo userInfo) {
		return ResponseEntity.status(201).body(bookMarkService.removeBookmark(userInfo.getId(), postId));
	}
}
