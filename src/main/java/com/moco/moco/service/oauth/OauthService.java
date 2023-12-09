package com.moco.moco.service.oauth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moco.moco.dto.auth.OauthUserInfoDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OauthService {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
	private final String KAKAO_USERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";
	private final String GITHUB_USERINFO_REQUEST_URL = "https://api.github.com/user";

	public String requestGoogleUserInfo(String accessToken) {
		ResponseEntity<String> response = requestUserInfo(GOOGLE_USERINFO_REQUEST_URL, "Bearer " + accessToken);
		return "google" + parseUserInfo(response);
	}

	public String requestKaKaoUserInfo(String accessToken) {
		ResponseEntity<String> response = requestUserInfo(KAKAO_USERINFO_REQUEST_URL, "Bearer " + accessToken);
		return "kakao" + parseUserInfo(response);
	}

	public String requestGithubUserInfo(String accessToken) {
		ResponseEntity<String> response = requestUserInfo(GITHUB_USERINFO_REQUEST_URL, "token " + accessToken);
		return "github" + parseUserInfo(response);
	}

	// 유저정보를 얻기위해 HTTP 요청한다.
	private ResponseEntity<String> requestUserInfo(String requestUrl, String header) {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity entity = new HttpEntity(headers);
		headers.set("Authorization", header);

		return restTemplate.exchange(
			requestUrl,
			HttpMethod.GET,
			entity,
			String.class);
	}

	// 받아온 유저정보(json)를 Class에 매핑시킨다.
	private String parseUserInfo(ResponseEntity<String> response) {
		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		return gson.fromJson(response.getBody(), OauthUserInfoDto.UserInfo.class).getId();
	}
}
