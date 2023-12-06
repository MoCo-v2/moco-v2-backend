package com.moco.moco.dto;

import com.moco.moco.domain.Like;
import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;

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
		private String userId;
		private Long postId;

		/* Entity -> DTO */
		public Response(Like like) {
			this.id = like.getId();
			this.userId = like.getUser().getId();
			this.postId = like.getPost().getId();
		}
	}
}
