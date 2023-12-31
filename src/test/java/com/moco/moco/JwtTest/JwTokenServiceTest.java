package com.moco.moco.JwtTest;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;

import com.moco.moco.repository.RefreshTokenRepository;
import com.moco.moco.service.jwt.JwTokenService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.Decoders;

@DisplayName("JWT CRUD 테스트 🧪")
/* @TestInstane는 테스트 인스턴스의 라이프 사이클을 설정할 때 사용한다.
	JUnit5는 테스트 인스턴스 생성 기본 단위가 메소드이다.
	각 메소드별로 따로 인스턴스가 생성되어 테스트 되기 때문에 메소드가 격리 되어있어 단위 테스트에 용이하다.
	하지만 메소드간 영향을 주는 테스트 케이스인 경우 (ex.변수 공유) @TestInstance 를 사용하면 된다.
*/

// PER_CLASS: test 클래스 당 인스턴스가 생성
// PER_METHOD: test 함수 당 인스턴스가 생성
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwTokenServiceTest {

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	private static JwTokenService jwTokenService;

	private final String secretKey = "quswowlsquswowlsquswowlsquswowlsquswasasasowasdasdls";
	private final String USER_ID = "google1234567";
	private final String subject = "access Token";
	private String base64EncodedSecretKey;

	// 테스트에 사용할 Secret Key를 Base64 형식으로 인코딩한 후,
	// 인코딩된 Secret Key를 각 테스트 케이스에서 사용
	@BeforeAll
	public void init() {
		jwTokenService = new JwTokenService(refreshTokenRepository);
		base64EncodedSecretKey = jwTokenService.encodeBase64SecretKey(secretKey);
		System.out.println(base64EncodedSecretKey);
	}

	// 평문인 Secret Key가 Base64 형식으로 인코딩이 정상적으로 수행되는지 테스트
	@Test
	public void encodeBase64SecretKeyTest() {
		System.out.println(base64EncodedSecretKey);
		assertThat(secretKey, is(new String(Decoders.BASE64.decode(base64EncodedSecretKey))));
	}

	private Map<String, Object> generateClaims() {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", USER_ID);
		claims.put("roles", List.of("USER"));
		return claims;
	}

	private Date getTimeAfterMinute(int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	private Date getTimeAfterHour(int hour) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, hour);
		return calendar.getTime();
	}

	private Date getTimeAfterSecond(int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, second);
		return calendar.getTime();
	}

	@DisplayName("Access Token 생성 정상 케이스")
	@Test
	public void generateAccessTokenTest() {
		//given
		Map<String, Object> claims = generateClaims();
		Date expiration = getTimeAfterMinute(110);

		//when
		String accessToken = jwTokenService.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

		System.out.println(accessToken);

		//then
		assertThat(accessToken, notNullValue());
	}

	@DisplayName("Refresh Token 생성 정상 케이스")
	@Test
	public void generateRefreshTokenTest() {

		//given
		Date expiration = getTimeAfterHour(24);

		// when
		String refreshToken = jwTokenService.generateRefreshToken(subject, expiration, base64EncodedSecretKey);

		System.out.println(refreshToken);

		//then
		assertThat(refreshToken, notNullValue());
	}

	@DisplayName("토큰 검증 정상 케이스")
	@Test
	public void verifySignatureTest() {
		//given
		Map<String, Object> claims = generateClaims();
		Date expiration = getTimeAfterMinute(10);

		//when
		String accessToken = jwTokenService.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

		//then
		assertDoesNotThrow(() -> jwTokenService.verifySignature(accessToken, base64EncodedSecretKey));
	}

	@DisplayName("만료 기간이 지난 토큰 검증")
	@Test
	public void verifyExpirationTest() throws InterruptedException {
		//given
		Map<String, Object> claims = generateClaims();
		Date expiration = getTimeAfterSecond(1);

		String accessToken = jwTokenService.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

		//when
		assertDoesNotThrow(() -> jwTokenService.verifySignature(accessToken, base64EncodedSecretKey));

		TimeUnit.MILLISECONDS.sleep(1500);

		//then
		assertThrows(ExpiredJwtException.class,
			() -> jwTokenService.verifySignature(accessToken, base64EncodedSecretKey));
	}

}
