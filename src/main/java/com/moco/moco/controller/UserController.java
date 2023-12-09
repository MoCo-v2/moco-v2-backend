package com.moco.moco.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.dto.CommonResponseDto;
import com.moco.moco.dto.UserDto;
import com.moco.moco.dto.auth.TokenDto;
import com.moco.moco.service.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController {

	private final UserService userService;

	@PostMapping("/public/login")
	public ResponseEntity<TokenDto.Response> login(@Valid @RequestBody TokenDto.OauthRequest tokenDto) {
		return ResponseEntity.ok().body(userService.authenticateAndGenerateToken(tokenDto));
	}

	@PostMapping("/public/join")
	public ResponseEntity<TokenDto.Response> signUp(@Valid @RequestBody UserDto.Request request) {
		return ResponseEntity.ok().body(userService.join(request));
	}

	/* 별명 중복 체크 */
	@GetMapping("/public/check-nickname/{name}")
	public ResponseEntity<CommonResponseDto> checkNameDuplication(@PathVariable(value = "name") String name) {
		boolean isNameDuplication = userService.checkNameDuplication(name);
		if (isNameDuplication) {
			return ResponseEntity.status(409)
				.body(CommonResponseDto.builder().msg("이미 존재하는 이름입니다.").result(false).build());
		}
		return ResponseEntity.ok().body(CommonResponseDto.builder().msg("사용 가능한 이름입니다.").result(true).build());
	}

}