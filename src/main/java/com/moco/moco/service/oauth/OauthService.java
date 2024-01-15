package com.moco.moco.service.oauth;

import static com.moco.moco.common.Constants.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moco.moco.domain.OauthType;
import com.moco.moco.dto.auth.OauthUserInfoDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OauthService {

	private final RestTemplate restTemplate = new RestTemplate();

	public String getOauth2UserId(OauthType oauthType, String accessToken) {
		return switch (oauthType) {
			case GOOGLE -> requestGoogleUserInfo(accessToken);
			case KAKAO -> requestKaKaoUserInfo(accessToken);
			case GITHUB -> requestGithubUserInfo(accessToken);
		};
	}

	private String requestGoogleUserInfo(String accessToken) {
		ResponseEntity<String> response = requestUserInfo(GOOGLE_USERINFO_REQUEST_URL, BEARER + accessToken);
		return OauthType.GOOGLE.name().toLowerCase() + parseUserInfo(response);
	}

	private String requestKaKaoUserInfo(String accessToken) {
		ResponseEntity<String> response = requestUserInfo(KAKAO_USERINFO_REQUEST_URL, BEARER + accessToken);
		return OauthType.KAKAO.name().toLowerCase() + parseUserInfo(response);
	}

	private String requestGithubUserInfo(String accessToken) {
		ResponseEntity<String> response = requestUserInfo(GITHUB_USERINFO_REQUEST_URL, TOKEN + accessToken);
		return OauthType.GITHUB.name().toLowerCase() + parseUserInfo(response);
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
