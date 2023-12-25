package com.moco.moco.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.dto.auth.TokenDto;
import com.moco.moco.service.jwt.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class TokenController {
	private final RefreshTokenService refreshTokenService;

	@PostMapping("/public/access-token")
	public ResponseEntity<TokenDto.AccessTokenRespose> generateAccessToken(
		@RequestBody TokenDto.AccessTokenRequest request) {
		String accessToken = refreshTokenService.generateAccessToken(request.getRefreshToken());
		return ResponseEntity.status(201).body(new TokenDto.AccessTokenRespose(accessToken));
	}
}
