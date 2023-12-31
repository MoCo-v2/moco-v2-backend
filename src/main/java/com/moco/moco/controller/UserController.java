package com.moco.moco.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.config.argsResolver.CurrentLoginUser;
import com.moco.moco.config.argsResolver.UserInfo;
import com.moco.moco.dto.CommonResponseDto;
import com.moco.moco.dto.UserDto;
import com.moco.moco.dto.auth.TokenDto;
import com.moco.moco.service.jwt.JwTokenService;
import com.moco.moco.service.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController {

	private final UserService userService;
	private final JwTokenService jwTokenService;

	@GetMapping("/private/users")
	public ResponseEntity<UserDto.Response> getUserProfile(@CurrentLoginUser UserInfo userInfo) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(userInfo.getId()));
	}

	@PostMapping("/public/login")
	public ResponseEntity<TokenDto.Response> login(@Valid @RequestBody TokenDto.OauthRequest tokenDto) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.authenticateAndGenerateToken(tokenDto));
	}

	@PostMapping("/public/join")
	public ResponseEntity<TokenDto.Response> signUp(@Valid @RequestBody UserDto.Request userDto) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.join(userDto));
	}

	@DeleteMapping("/private/logout")
	public ResponseEntity<?> logout(@RequestBody TokenDto.AccessTokenRequest request,
		@CurrentLoginUser UserInfo userInfo) {
		jwTokenService.removeRefreshToken(request.getRefreshToken(), userInfo.getId());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/public/check-nickname/{name}")
	public ResponseEntity<CommonResponseDto> checkNameDuplication(@PathVariable(value = "name") String name) {
		boolean isNameDuplication = userService.checkNameDuplication(name);
		if (isNameDuplication) {
			return ResponseEntity.status(409)
				.body(CommonResponseDto.builder().msg("이미 존재하는 이름입니다.").result(false).build());
		}
		return ResponseEntity.status(HttpStatus.OK)
			.body(CommonResponseDto.builder().msg("사용 가능한 이름입니다.").result(true).build());
	}

	@PutMapping("/private/users")
	public ResponseEntity<UserDto.Response> updateUserInfo(@Valid @RequestBody UserDto.Request userDto) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserInfo(userDto));
	}

}