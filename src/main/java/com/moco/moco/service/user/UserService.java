package com.moco.moco.service.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.client.RestTemplate;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moco.moco.domain.User;
import com.moco.moco.dto.UserDto;
import com.moco.moco.dto.auth.OauthUserInfoDto;
import com.moco.moco.dto.auth.TokenDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.repository.PostRepository;
import com.moco.moco.repository.UserRepository;
import com.moco.moco.service.token.JwTokenService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PostRepository boardRepository;

	private final JwTokenService tokenService;
	private final RestTemplate restTemplate = new RestTemplate();
	private final String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

	/**
	 * 유저 정보를 구글, 카카오, 깃허브 서버로 요청한다.
	 * @param tokenDto
	 * @return
	 */
	public TokenDto.Response authenticateAndGenerateToken(TokenDto.OauthRequest tokenDto) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + tokenDto.getAccessToken());
		HttpEntity entity = new HttpEntity(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				GOOGLE_USERINFO_REQUEST_URL,
				HttpMethod.GET,
				entity,
				String.class);
			Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

			OauthUserInfoDto.Google userInfo = gson.fromJson(response.getBody(), OauthUserInfoDto.Google.class);

			//기존 회원 or 새로운 사용자인지 검증한다.
			boolean newUser = !userRepository.existsByEmail(userInfo.getEmail());

			if (newUser) {
				return TokenDto.Response.builder()
					.accessToken("null")
					.refreshToken("null")
					.email(userInfo.getEmail())
					.result(false)
					.build();
			}
			
			//Refactoring 필요 👨🏻‍🔧
			Map<String, Object> claims = tokenService.generateClaims(userInfo.getEmail());
			String subject = "access token";

			String secretKey = tokenService.encodeBase64SecretKey(tokenService.getSecretKey());

			Date accessTokenExpiration = tokenService.getTimeAfterMinutes(60);
			String accessToken = tokenService.generateAccessToken(claims, subject, accessTokenExpiration, secretKey);

			Date refreshTokenExpiration = tokenService.getTimeAfterWeeks(2);
			String refreshToken = tokenService.generateRefreshToken(subject, refreshTokenExpiration, secretKey);

			return TokenDto.Response.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.email(null)
				.result(true)
				.build();

		} catch (Exception e) {
			throw new CustomAuthenticationException(ErrorCode.INVALID_AUTH_TOKEN);
		}
	}

	/* 회원가입 */
	@Transactional
	public User join(UserDto.Request userDto) {
		return userRepository.save(userDto.toEntity());
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

	@Transactional(readOnly = true)
	public boolean emailExists(String email) {
		return userRepository.existsByEmail(email);
	}

	/* 회원가입시 별명 중복 여부 */
	@Transactional(readOnly = true)
	public boolean checkUsernameDuplication(String name) {
		boolean usernameDuplication = userRepository.existsByName(name);

		return usernameDuplication;
	}

	/* 설정에서 별명 바꾸기 */
	@Transactional
	public User nameUpdateInSetting(Long userid, String name) {
		User user = userRepository.findById(userid).orElseThrow(() ->
			new IllegalArgumentException("유저를 찾을수 없습니다."));
		user.updateNameInSetting(name);
		/* 해당 유저가 작성한 게시글 작성자도 변경 */
		boardRepository.updateWriter(name, user.getId());
		return user;
	}

	/* 설정에서 프로필 사진 변경 */
	@Transactional
	public User profileUpdateInSetting(Long userId, String imgURL) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		user.updateProfile(imgURL);
		return user;
	}

	/* 회원탈퇴 */
	@Transactional
	public void deleteUser(Long userId) {
		userRepository.deleteById(userId);
	}
}
