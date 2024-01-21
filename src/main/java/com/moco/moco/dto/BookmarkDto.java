package com.moco.moco.dto;

import java.util.List;

import com.moco.moco.domain.Bookmark;

import lombok.Getter;

public class BookmarkDto {

	@Getter
	public static class Response {
		private Long id;
		private String userId;
		private Long postId;

		public Response(Bookmark bookmark) {
			this.id = bookmark.getId();
			this.userId = bookmark.getUser().getId();
			this.postId = bookmark.getPost().getId();
		}
	}

	@Getter
	public static class MyBookmark {
		private final List<Long> idList;

		public MyBookmark(List<Long> ids) {
			this.idList = ids;
		}
	}
}
