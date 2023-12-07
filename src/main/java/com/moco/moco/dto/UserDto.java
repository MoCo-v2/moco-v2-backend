package com.moco.moco.dto;

import com.moco.moco.domain.Role;
import com.moco.moco.domain.User;

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
		@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
		private String name;
		private String picture;
		private Role role;

		/* DTO -> Entity */
		public User toEntity() {
			return User
				.builder()
				.name(name)
				.picture(picture)
				.role(Role.USER)
				.build();
		}
	}

	@Getter
	public static class Response {
		private String name;
		private String picture;

		/* Entity -> Dto */
		public Response(User user) {
			this.name = user.getName();
			this.picture = user.getPicture();
		}
	}
}
