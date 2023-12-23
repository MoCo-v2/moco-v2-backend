package com.moco.moco.dto;

import com.moco.moco.domain.Role;
import com.moco.moco.domain.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

public class UserDto {

	@Getter
	public static class Request {
		@NotEmpty
		@Pattern(regexp = "^(google|kakao|github).*", message = "유효한 ID가 아닙니다.")
		private String id;
		@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
		private String name;
		private String intro;
		private String position;
		private String career;
		private String stack;
		private String picture;

		/* DTO -> Entity */
		public User toEntity() {
			return User
				.builder()
				.id(id)
				.name(name)
				.intro(intro)
				.position(position)
				.career(career)
				.stack(stack)
				.picture(picture)
				.role(Role.USER)
				.build();
		}
	}

	@Getter
	public static class Response {
		private String id;
		private String name;
		private String intro;
		private String position;
		private String career;
		private String stack;
		private String picture;

		/* Entity -> Dto */
		public Response(User user) {
			this.id = user.getId();
			this.name = user.getName();
			this.intro = user.getIntro();
			this.position = user.getPosition();
			this.career = user.getCareer();
			this.stack = user.getStack();
			this.picture = user.getPicture();
		}
	}
}
