package com.board.board.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.board.board.dto.auth.TokenDto;
import com.board.board.service.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class LoginController {

	private final UserService userService;

	@PostMapping("/login")
	public ResponseEntity<TokenDto.Response> login(@Valid @RequestBody TokenDto.OauthRequest tokenDto) {
		return ResponseEntity.ok().body(userService.authenticateAndGenerateToken(tokenDto));
	}

}