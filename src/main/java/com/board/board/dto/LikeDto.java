package com.board.board.dto;

import com.board.board.domain.Like;
import com.board.board.domain.Post;
import com.board.board.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LikeDto {
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class Request {
		private Long id;
		private User user;
		private Post post;

		/* DTO -> Entity */
		public Like toEntity() {
			Like like = Like.builder().id(id).user(user).post(post).build();
			return like;
		}
	}

	@Getter
	public static class Response {
		private Long id;
		private Long userId;
		private Long postId;

		/* Entity -> DTO */
		public Response(Like like) {
			this.id = like.getId();
			this.userId = like.getUser().getId();
			this.postId = like.getPost().getId();
		}
	}
}
