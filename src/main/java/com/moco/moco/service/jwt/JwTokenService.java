package com.moco.moco.service.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.moco.moco.config.argsResolver.UserInfo;
import com.moco.moco.domain.RefreshToken;
import com.moco.moco.domain.Role;
import com.moco.moco.domain.User;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.repository.RefreshTokenRepository;
import com.moco.moco.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwTokenService {
	@Getter
	@Value("${jwt.key.secret}")
	private String secretKey;
	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	// 평문 비밀키를 Base64로 인코딩한다.
	// 인코딩 하는 이유는 OS마다 특수문자를 바이너리 값으로 치환하는 방법이 다르기 때문에 미리 어플리케이션 레벨에서 변환해주기 위함
	public String encodeBase64SecretKey(String secretKey) {
		return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	// Base64로 인코딩된 키를 디코딩후 적절한 HMAC 알고리즘을 이용한 Key 객체를 생성한다.
	// 이 키로 서명한다.최신 버전의 jjwt 에서는 내부적으로 적절한 HMAC 알고리즘을 지정해 준다.
	private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
		Key key = Keys.hmacShaKeyFor(keyBytes);

		return key;
	}

	// Access Token을 생성한다.
	public String generateAccessToken(
		Map<String, Object> claims,
		String subject,
		Date expiration,
		String base64EncodedSecretKey) {

		Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

		return Jwts.builder()
			.setClaims(claims)          // JWT에 포함 시킬 Custom Claims를 추가함 (Custom Claims에는 주로 인증된 사용자와 관련된 정보를 추가)
			.setSubject(subject)        // JWT에 대한 제목을 추가
			.setIssuedAt(Calendar.getInstance().getTime())   // JWT 발행 일자를 설정, 파라미터 타입은 java.util.Date 타입
			.setExpiration(expiration)  // JWT의 만료일시를 지정
			.signWith(key)              // 서명을 위한 Key 객체를 설정
			.compact();                 // JWT를 생성하고 직렬화함
	}

	// Refresh Token을 생성한다.
	public String generateRefreshToken(
		String subject,
		Date expiration,
		String base64EncodedSecretKey) {

		Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

		return Jwts.builder()
			.setSubject(subject)
			.setIssuedAt(Calendar.getInstance().getTime())
			.setExpiration(expiration)
			.signWith(key)
			.compact();
	}

	public String renewAccessToken(String refreshToken) {
		RefreshToken getRefreshToken = refreshTokenRepository.findById(refreshToken)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.NEED_LOGIN));

		User user = userRepository.findById(getRefreshToken.getUserId())
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));

		Map<String, Object> claims = null;

		if (user.getRole().getKey().equals(Role.MASTER.getKey())) {
			claims = generateAdminClaims(user.getId());
		} else {
			claims = generateUserClaims(user.getId());
		}

		String subject = "access token";
		String secretKey = encodeBase64SecretKey(getSecretKey());
		Date accessTokenExpiration = getTimeAfterMinutes(60);
		String accessToken = generateAccessToken(claims, subject, accessTokenExpiration, secretKey);
		return accessToken;
	}

	@Transactional
	public void saveTokenInfo(String refreshToken, String userId) {
		refreshTokenRepository.save(new RefreshToken(refreshToken, userId));
	}

	@Transactional
	public void removeRefreshToken(String refreshToken, String userId) {
		refreshTokenRepository.findById(refreshToken)
			.ifPresent(token -> {
				if (token.getUserId().equals(userId)) {
					refreshTokenRepository.delete(token);
				} else {
					throw new CustomAuthenticationException(ErrorCode.INVALID_AUTH_TOKEN);
				}
			});
	}

	// 토큰을 검증한다.
	// 토큰 검증에 실패하면 다양한 예외를 발생시킨다.
	// SignatureException : 서명 오류
	// MalformedJwtException : JWT 구조 오류
	// ExpiredJwtException : 만료 기간이 지난 토큰
	public UserInfo verifySignature(String jws, String base64EncodedSecretKey) {
		Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

		Claims claimsBody = Jwts.parserBuilder()
			.setSigningKey(key) // 서명에 사용된 Secret Key를 설정
			.build()
			.parseClaimsJws(jws).getBody(); // JWT를 파싱해서 Claims를 얻음
		return UserInfo
			.builder()
			.id((String)claimsBody.get("id"))
			.roles((List)claimsBody.get("roles"))
			.build();
	}

	public Map<String, Object> generateUserClaims(String userId) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", userId);
		claims.put("roles", List.of(Role.USER.getKey()));

		return claims;
	}

	public Map<String, Object> generateAdminClaims(String userId) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", userId);
		claims.put("roles", List.of(Role.MASTER.getKey()));

		return claims;
	}

	//입력받은 minutes이 지난 시간을 가져온다.
	public Date getTimeAfterMinutes(int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	//입력받은 weeks 지난 시간을 가져온다.
	public Date getTimeAfterWeeks(int weeks) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_WEEK, weeks);
		return calendar.getTime();
	}

}