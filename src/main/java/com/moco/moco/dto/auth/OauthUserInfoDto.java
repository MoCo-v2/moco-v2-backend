package com.moco.moco.dto.auth;

import lombok.Getter;
import lombok.Setter;

public class OauthUserInfoDto {

	@Setter
	@Getter
	public static class Google {
		private String id;
		private String email;
		private String name;
		private String picture;
	}

	@Getter
	public static class Kakao {
		private String id;
		private String email;
		private String name;
		private String picture;
		private String locale;
	}

	@Getter
	public static class Github {
		private String id;
		private String email;
		private String name;
		private String picture;
		private String locale;
	}
}
