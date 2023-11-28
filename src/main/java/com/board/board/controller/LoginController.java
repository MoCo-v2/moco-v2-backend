package com.board.board.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.board.board.dto.TokenDto;

@RestController
public class LoginController {

	private final RestTemplate restTemplate = new RestTemplate();
	private final String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody TokenDto.OauthRequest accessTokenDto) {
		System.out.println(accessTokenDto.getAccessToken());
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessTokenDto.getAccessToken());
		HttpEntity entity = new HttpEntity(headers);

		ResponseEntity<String> exchange = restTemplate.exchange(
			GOOGLE_USERINFO_REQUEST_URL,
			HttpMethod.GET,
			entity,
			String.class);

		return exchange;
	}

}