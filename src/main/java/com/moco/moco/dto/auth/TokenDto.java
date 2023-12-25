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

	public abstract static class Response {

	}

	@Getter
	public static class JwtResponse extends Response {
		private String accessToken;
		private String refreshToken;
		private Boolean result;

		@Builder
		public JwtResponse(String accessToken, String refreshToken, Boolean result) {
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
			this.result = result;
		}

	}

	@Getter
	public static class IdResponse extends Response {
		private String id;
		private Boolean result;

		@Builder
		public IdResponse(String id, Boolean result) {
			this.id = id;
			this.result = result;
		}
	}

	@Getter
	public static class AccessTokenRequest {
		private String refreshToken;
	}

	@Getter
	public static class AccessTokenRespose {
		private String accessToken;

		public AccessTokenRespose(String accessToken) {
			this.accessToken = accessToken;
		}
	}
}
