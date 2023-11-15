package com.board.board.dto;

import com.board.board.domain.Post;
import com.board.board.domain.Recruit;
import com.board.board.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RecruitDto {

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class Request {
		private Long id;
		private User user;
		private Post post;

		/* DTO -> Entity */
		public Recruit toEntity() {
			return Recruit.builder().id(id).user(user).post(post).build();
		}
	}

	@Getter
	public static class Response {
		private Long id;
		private Long userId;
		private Long postId;

		/* Entity -> DTO */
		public Response(Recruit recruit) {
			this.id = recruit.getId();
			this.userId = recruit.getUser().getId();
			this.postId = recruit.getPost().getId();
		}
	}
}
