package com.moco.moco.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.dto.auth.TokenDto;
import com.moco.moco.service.jwt.JwTokenService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class TokenController {
	private final JwTokenService jwTokenService;

	@PostMapping("/public/access-token")
	public ResponseEntity<TokenDto.AccessTokenRespose> generateAccessToken(
		@RequestBody TokenDto.AccessTokenRequest request) {
		String accessToken = jwTokenService.renewAccessToken(request.getRefreshToken());
		return ResponseEntity.status(HttpStatus.CREATED).body(new TokenDto.AccessTokenRespose(accessToken));
	}
}
