package com.moco.moco.common;

public class Constants {

	// Oauth Request Url
	public final static String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
	public final static String KAKAO_USERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";
	public final static String GITHUB_USERINFO_REQUEST_URL = "https://api.github.com/user";

	// Header Token 접두사
	public final static String BEARER = "Bearer ";
	public final static String TOKEN = "Token ";

	// Jwt Subject
	public final static String SUBJECT = "access Token";

	// 검색 조건
	public final static String ALL = "ALL";
}
