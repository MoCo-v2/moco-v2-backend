package com.board.board.dto;

import com.board.board.domain.Role;
import com.board.board.domain.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Request {
		@NotBlank(message = "아이디는 필수 입력 값입니다.")
		private String email;

		@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
		private String name;
		private String picture;
		private Role role;

		/* DTO -> Entity */
		public User toEntity() {
			return User
				.builder()
				.name(name)
				.email(email)
				.picture(picture)
				.role(Role.USER)
				.build();
		}
	}

	@Getter
	public static class Response {
		private String email;
		private String name;
		private String picture;

		/* Entity -> Dto */
		public Response(User user) {
			this.email = user.getEmail();
			this.name = user.getName();
			this.picture = user.getPicture();
		}
	}
}
