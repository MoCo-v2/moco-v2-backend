package com.board.board.service.token;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtTokenService {
	@Getter
	@Value("${jwt.key.secret}")
	private String secretKey;

	@Getter
	@Value("${jwt.access-token-expiration-millisecond}")
	private int accessTokenExpirationMillisecond;

	@Getter
	@Value("${jwt.refresh-token-expiration-millisecond}")
	private int refreshTokenExpirationMillisecond;

	public String encodeBase64SecretKey(String secretKey) {
		return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	//accessToken 생성
	public String generateAccessToken(String email) {
		Claims claims = Jwts.claims();
		claims.put("email", email);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMillisecond))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	//refreshToken 생성
	public String generateRefreshToken(String email) {
		Claims claims = Jwts.claims();
		claims.put("email", email);

		return Jwts.builder()
			.setIssuedAt(Calendar.getInstance().getTime())
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMillisecond))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	// 토큰 검증
	public boolean isExpired(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
			.getBody().getExpiration().before(new Date());
	}

	// email 가져오기
	public Jws<Claims> getClaims(String jws) {
		//Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

		Jws<Claims> claims = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(jws);

		return claims;
	}

	//토큰 만료 시간 가져오기
	public Date getTokenExpiration(int expirationMillisecond) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MILLISECOND, expirationMillisecond);
		Date expiration = calendar.getTime();

		return expiration;
	}

	private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
		Key key = Keys.hmacShaKeyFor(keyBytes);

		return key;
	}
}