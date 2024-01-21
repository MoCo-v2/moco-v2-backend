package com.moco.moco.controller;

import static com.moco.moco.common.ResponseEntityConstants.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
		BookmarkDto.Response bookmarkDto = bookMarkService.createBookmark(userInfo.getId(), postId);
		return ResponseEntity.status(HttpStatus.CREATED).body(bookmarkDto);
	}

	@DeleteMapping("/private/bookmark/{postId}")
	public ResponseEntity<HttpStatus> removeBookmark(@PathVariable Long postId,
		@CurrentLoginUser UserInfo userInfo) {
		bookMarkService.removeBookmark(userInfo.getId(), postId);
		return RESPONSE_ENTITY_NO_CONTENT;
	}

	@GetMapping("/private/bookmark")
	public ResponseEntity<BookmarkDto.MyBookmark> getMyBookmarks(@CurrentLoginUser UserInfo userInfo) {
		BookmarkDto.MyBookmark myBookmarks = bookMarkService.getMyBookmarks(userInfo.getId());
		return ResponseEntity.ok().body(myBookmarks);
	}
}
