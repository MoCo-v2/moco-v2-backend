package com.moco.moco.service.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.moco.moco.domain.User;
import com.moco.moco.dto.UserDto;
import com.moco.moco.dto.auth.TokenDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.repository.PostRepository;
import com.moco.moco.repository.UserRepository;
import com.moco.moco.service.oauth.OauthService;
import com.moco.moco.service.token.JwTokenService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PostRepository boardRepository;

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
			System.out.println(e.getMessage());
			throw new CustomAuthenticationException(ErrorCode.INVALID_AUTH_TOKEN);
		}
	}

	/* 회원가입 */
	@Transactional
	public TokenDto.JwtResponse join(UserDto.Request userDto) {
		User user = userRepository.save(userDto.toEntity());
		return getUserIdAndGenerateToken(user.getId());
	}

	/* 회원가입 시 input 유효성 체크 */
	@Transactional(readOnly = true)
	public Map<String, String> validateHandling(Errors errors) {
		Map<String, String> validatorResult = new HashMap<>();

		/* 유효성 검사에 실패한 필드 목록을 받음 */
		for (FieldError error : errors.getFieldErrors()) {
			String validKeyName = String.format("valid_%s", error.getField());
			validatorResult.put(validKeyName, error.getDefaultMessage());
		}
		return validatorResult;
	}

	/* 회원가입시 별명 중복 여부 */
	@Transactional(readOnly = true)
	public boolean checkNameDuplication(String name) {
		return userRepository.existsByName(name);
	}

	/* 설정에서 별명 바꾸기 */
	@Transactional
	public User nameUpdateInSetting(String userid, String name) {
		User user = userRepository.findById(userid).orElseThrow(() ->
			new IllegalArgumentException("유저를 찾을수 없습니다."));
		user.updateNameInSetting(name);
		/* 해당 유저가 작성한 게시글 작성자도 변경 */
		boardRepository.updateWriter(name, user.getId());
		return user;
	}

	/* 설정에서 프로필 사진 변경 */
	@Transactional
	public User profileUpdateInSetting(String userId, String imgURL) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		user.updateProfile(imgURL);
		return user;
	}

	/* 회원탈퇴 */
	@Transactional
	public void deleteUser(String userId) {
		userRepository.deleteById(userId);
	}

	private TokenDto.JwtResponse getUserIdAndGenerateToken(String userId) {
		//기존 사용자라면 JWT 발급한다.
		Map<String, Object> claims = tokenService.generateClaims(userId);

		String subject = "access token";

		String secretKey = tokenService.encodeBase64SecretKey(tokenService.getSecretKey());

		Date accessTokenExpiration = tokenService.getTimeAfterMinutes(60);
		String accessToken = tokenService.generateAccessToken(claims, subject, accessTokenExpiration, secretKey);

		Date refreshTokenExpiration = tokenService.getTimeAfterWeeks(2);
		String refreshToken = tokenService.generateRefreshToken(subject, refreshTokenExpiration, secretKey);

		return TokenDto.JwtResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.result(true)
			.build();
	}
}
