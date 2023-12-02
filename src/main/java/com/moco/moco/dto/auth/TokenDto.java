package com.moco.moco.dto.auth;

import com.moco.moco.domain.OauthType;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

public class TokenDto {
	@Getter
	public static class OauthRequest {
		@NotNull
		private OauthType provider;
		@NotNull
		private String accessToken;

		//Json 직렬화 하기 위해 기본 생성자 필요
		public OauthRequest() {

		}

		public OauthRequest(OauthType provider, String accessToken) {
			this.provider = provider;
			this.accessToken = accessToken;
		}
	}

	@Getter
	public static class Response {
		private String accessToken;
		private String refreshToken;
		private String email;
		private Boolean result;

		@Builder
		public Response(String accessToken, String refreshToken, String email, Boolean result) {
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
			this.email = email;
			this.result = result;
		}
	}
}
