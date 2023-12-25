package com.moco.moco.service.jwt;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.moco.moco.domain.RefreshToken;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwTokenService tokenService;

	@Transactional
	public void saveTokenInfo(String refreshToken, String userId) {
		refreshTokenRepository.save(new RefreshToken(refreshToken, userId));
	}

	@Transactional
	public void removeRefreshToken(String refreshToken) {
		refreshTokenRepository.findById(refreshToken)
			.ifPresent(token -> refreshTokenRepository.delete(token));
	}

	public String generateAccessToken(String refreshToken) {
		RefreshToken getRefreshToken = refreshTokenRepository.findById(refreshToken)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.BAD_REQUEST));

		Map<String, Object> claims = tokenService.generateClaims(getRefreshToken.getUserId());

		String subject = "access token";
		String secretKey = tokenService.encodeBase64SecretKey(tokenService.getSecretKey());
		Date accessTokenExpiration = tokenService.getTimeAfterMinutes(60);
		String accessToken = tokenService.generateAccessToken(claims, subject, accessTokenExpiration, secretKey);
		return accessToken;
	}
}
