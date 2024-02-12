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

	@Transactional
	public TokenDto.Response authenticateAndGenerateToken(TokenDto.OauthRequest tokenDto) {

		try {

			String userId = oauthService.getUserIdFromOauthService(tokenDto.getProvider(), tokenDto.getAccessToken());

			Optional<User> userOptional = userRepository.findById(userId);

			//새로운 사용자라면 탈퇴 상태로 가입 시킨다.
			if (userOptional.isEmpty()) {
				return registerNewUser(userId);
			}

			User user = userOptional.get();
			if (user.isDeleted()) {
				return buildIdResponse(userId, false);
			}

			return getUserIdAndGenerateToken(userId);

		} catch (Exception e) {
			log.error(e.getMessage(), UserService.class);
			throw new CustomAuthenticationException(ErrorCode.INVALID_AUTH_TOKEN);
		}
	}

	private TokenDto.Response registerNewUser(String userId) {
		User newUser = User.builder().id(userId).name(userId).isDeleted(true).build();
		userRepository.save(newUser);
		return buildIdResponse(userId, false);
	}

	private TokenDto.Response buildIdResponse(String userId, boolean result) {
		return TokenDto.IdResponse.builder()
			.id(userId)
			.result(result)
			.build();
	}

	@Transactional
	public TokenDto.JwtResponse join(UserDto.Request userDto) {
		validationUserId(userDto.getId());

		User user = userRepository.findById(userDto.getId())
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));

		if (!user.isDeleted()) {
			throw new CustomAuthenticationException(ErrorCode.DUPLICATE_RESOURCE);
		}

		user.join();
		user.update(userDto);

		return getUserIdAndGenerateToken(user.getId());
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

}
