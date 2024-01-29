package com.moco.moco.service.user;

import static com.moco.moco.common.Constants.*;
import static com.moco.moco.common.Validation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moco.moco.config.argsResolver.UserInfo;
import com.moco.moco.domain.User;
import com.moco.moco.dto.UserDto;
import com.moco.moco.dto.auth.TokenDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.jpaRepository.UserRepository;
import com.moco.moco.service.jwt.JwTokenService;
import com.moco.moco.service.oauth.OauthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final JwTokenService tokenService;
	private final OauthService oauthService;

	public UserDto.Response getUser(String userId) {
		validationUserId(userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));
		return new UserDto.Response(user);
	}

	public UserDto.Response getMyInfo(UserInfo userInfo) {
		validationUserId(userInfo.getId());

		User user = userRepository.findById(userInfo.getId())
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));
		return new UserDto.Response(user);
	}

	public TokenDto.Response authenticateAndGenerateToken(TokenDto.OauthRequest tokenDto) {

		try {

			String userId = oauthService.getOauth2UserId(tokenDto.getProvider(), tokenDto.getAccessToken());

			Optional<User> user = userRepository.findById(userId);

			//새로운 사용자라면 유저 ID를 반환하여 회원가입을 진행한다.
			if (shouldProceedWithRegistration(user)) {
				return TokenDto.IdResponse.builder()
					.id(userId)
					.result(false)
					.build();
			}

			return getUserIdAndGenerateToken(user.get().getId());

		} catch (Exception e) {
			log.error(e.getMessage(), UserService.class);
			throw new CustomAuthenticationException(ErrorCode.INVALID_AUTH_TOKEN);
		}
	}

	@Transactional
	public TokenDto.JwtResponse join(UserDto.Request userDto) {
		validationUserId(userDto.getId());

		Optional<User> existingUser = userRepository.findById(userDto.getId());

		if (existingUser.isPresent()) {
			User user = existingUser.get();
			if (!user.isDeleted()) {
				throw new CustomAuthenticationException(ErrorCode.DUPLICATE_RESOURCE);
			}
			user.join();
			return getUserIdAndGenerateToken(user.getId());
		}

		User savedUser = userRepository.save(userDto.toEntity());
		return getUserIdAndGenerateToken(savedUser.getId());
	}

	@Transactional
	public UserDto.Response updateUserInfo(UserDto.Request userDto) {
		validationUserId(userDto.getId());

		User user = userRepository.findById(userDto.getId())
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));

		user.update(userDto);

		return new UserDto.Response(user);
	}

	@Transactional(readOnly = true)
	public boolean checkNameDuplication(String name) {
		return userRepository.existsByName(name);
	}

	private TokenDto.JwtResponse getUserIdAndGenerateToken(String userId) {
		validationUserId(userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));

		Map<String, Object> claims = tokenService.generateClaims(user.getRoleKey(), user.getId());

		String subject = SUBJECT;

		String secretKey = tokenService.encodeBase64SecretKey(tokenService.getSecretKey());

		Date accessTokenExpiration = tokenService.getTimeAfterMinutes(60);
		String accessToken = tokenService.generateAccessToken(claims, subject, accessTokenExpiration, secretKey);

		Date refreshTokenExpiration = tokenService.getTimeAfterWeeks(1);
		String refreshToken = tokenService.generateRefreshToken(subject, refreshTokenExpiration, secretKey);

		//redis에 RefreshToken 저장 (refreshToken, 유저 ID)
		tokenService.saveRefreshToken(refreshToken, userId);

		return TokenDto.JwtResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.result(true)
			.build();
	}

	@Transactional
	public void deleteUser(String userId) {
		validationUserId(userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));
		user.delete();
	}

	private boolean shouldProceedWithRegistration(Optional<User> user) {
		return user.isEmpty() || user.get().isDeleted();
	}
}
