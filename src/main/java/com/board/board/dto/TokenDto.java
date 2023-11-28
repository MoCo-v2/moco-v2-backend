package com.board.board.dto;

import lombok.Getter;

public class TokenDto {
	@Getter
	public static class OauthRequest {
		private String accessToken;

		//Json 직렬화 하기 위해 기본 생성자 필요
		public OauthRequest() {

		}

		public OauthRequest(String accessToken) {
			this.accessToken = accessToken;
		}
	}

	@Getter
	public static class JwtResponse {
		private String accessToken;
		private String refreshToken;

		public JwtResponse(String accessToken, String refreshToken) {
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
		}
	}
}
