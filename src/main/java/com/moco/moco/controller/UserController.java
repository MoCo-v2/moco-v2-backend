package com.moco.moco.controller;

import static com.moco.moco.common.ResponseEntityConstants.*;

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

	@GetMapping("/public/users/{userId}")
	public ResponseEntity<UserDto.Response> getUserInfo(@PathVariable String userId) {
		UserDto.Response userInfoDto = userService.getUser(userId);
		return ResponseEntity.status(HttpStatus.OK).body(userInfoDto);
	}

	@GetMapping("/private/users")
	public ResponseEntity<UserDto.Response> getMyInfo(@CurrentLoginUser UserInfo userInfo) {
		UserDto.Response userInfoDto = userService.getMyInfo(userInfo);
		return ResponseEntity.status(HttpStatus.OK).body(userInfoDto);
	}

	@PostMapping("/public/login")
	public ResponseEntity<TokenDto.Response> login(@Valid @RequestBody TokenDto.OauthRequest tokenDto) {
		TokenDto.Response generatedTokenDto = userService.authenticateAndGenerateToken(tokenDto);
		return ResponseEntity.status(HttpStatus.OK).body(generatedTokenDto);
	}

	@PostMapping("/public/join")
	public ResponseEntity<TokenDto.Response> signUp(@Valid @RequestBody UserDto.Request userDto) {
		TokenDto.JwtResponse generatedTokenDto = userService.join(userDto);
		return ResponseEntity.status(HttpStatus.OK).body(generatedTokenDto);
	}

	@DeleteMapping("/private/logout")
	public ResponseEntity<HttpStatus> logout(@RequestBody TokenDto.AccessTokenRequest request,
		@CurrentLoginUser UserInfo userInfo) {
		jwTokenService.removeRefreshToken(request.getRefreshToken(), userInfo.getId());
		return RESPONSE_ENTITY_NO_CONTENT;
	}

	@GetMapping("/public/check-nickname/{name}")
	public ResponseEntity<CommonResponseDto> checkNameDuplication(@PathVariable(value = "name") String name) {
		boolean isNameDuplication = userService.checkNameDuplication(name);
		if (isNameDuplication) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(CommonResponseDto.builder().msg("이미 존재하는 이름입니다.").result(false).build());
		}
		return ResponseEntity.status(HttpStatus.OK)
			.body(CommonResponseDto.builder().msg("사용 가능한 이름입니다.").result(true).build());
	}

	@PutMapping("/private/users")
	public ResponseEntity<UserDto.Response> updateUserInfo(@Valid @RequestBody UserDto.Request userDto) {
		UserDto.Response userInfoDto = userService.updateUserInfo(userDto);
		return ResponseEntity.status(HttpStatus.OK).body(userInfoDto);
	}

}