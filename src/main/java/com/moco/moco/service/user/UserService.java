package com.moco.moco.service.user;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moco.moco.domain.User;
import com.moco.moco.dto.UserDto;
import com.moco.moco.dto.auth.TokenDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.repository.UserRepository;
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

	public TokenDto.Response authenticateAndGenerateToken(TokenDto.OauthRequest tokenDto) {
		String id = "";

		try {

			switch (tokenDto.getProvider()) {
				case GOOGLE:
					id = oauthService.requestGoogleUserInfo(tokenDto.getAccessToken());
					break;
				case KAKAO:
					id = oauthService.requestKaKaoUserInfo(tokenDto.getAccessToken());
					break;
				case GITHUB:
					id = oauthService.requestGithubUserInfo(tokenDto.getAccessToken());
					break;
				default:
					throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
			}

			Optional<User> user = userRepository.findById(id);

			//새로운 사용자라면 유저 ID를 반환하여 회원가입을 진행한다.
			if (user.isEmpty()) {
				return TokenDto.IdResponse.builder()
					.id(id)
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
		boolean isExistId = userRepository.existsById(userDto.getId());
		if (isExistId) {
			throw new CustomAuthenticationException(ErrorCode.DUPLICATE_RESOURCE);
		}
		User user = userRepository.save(userDto.toEntity());
		return getUserIdAndGenerateToken(user.getId());
	}

	public void logout(String userId) {

	}

	@Transactional
	public UserDto.Response updateUserInfo(UserDto.Request userDto) {
		User user = userRepository.findById(userDto.getId())
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));

		user.update(userDto.getName(), userDto.getIntro(), userDto.getPosition(), userDto.getStack(),
			userDto.getCareer(), userDto.getPicture());

		return new UserDto.Response(user);
	}

	@Transactional(readOnly = true)
	public boolean checkNameDuplication(String name) {
		return userRepository.existsByName(name);
	}

	private TokenDto.JwtResponse getUserIdAndGenerateToken(String userId) {
		Map<String, Object> claims = tokenService.generateClaims(userId);

		String subject = "access token";

		String secretKey = tokenService.encodeBase64SecretKey(tokenService.getSecretKey());

		Date accessTokenExpiration = tokenService.getTimeAfterMinutes(60);
		String accessToken = tokenService.generateAccessToken(claims, subject, accessTokenExpiration, secretKey);

		Date refreshTokenExpiration = tokenService.getTimeAfterWeeks(1);
		String refreshToken = tokenService.generateRefreshToken(subject, refreshTokenExpiration, secretKey);

		//redis에 RefreshToken 저장 (refreshToken, 유저 ID)
		tokenService.saveTokenInfo(refreshToken, userId);

		return TokenDto.JwtResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.result(true)
			.build();
	}
}
