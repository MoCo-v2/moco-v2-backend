package com.board.board.config.auth;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.board.board.service.token.JwtTokenService;
import com.board.board.service.user.UserService;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OauthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtTokenService jwtTokenService;
	private final UserService userService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication)
		throws IOException, ServletException {
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

		log.info("Principal에서 꺼낸 OAuth2User = {}", oAuth2User);

		String email = oAuth2User.getAttribute("email");
		boolean isExist = oAuth2User.getAttribute("exist");
		String role = oAuth2User.getAuthorities()
			.stream()
			.findFirst()
			.orElseThrow(IllegalAccessError::new) // 존재하지 않을 시 예외를 던진다.
			.getAuthority(); // Role을 가져온다.

		if (isExist) {
			redirect(request, response, email, role);
		} else {
			redirectJoin(request, response, email, role);
		}
	}

	private void redirect(HttpServletRequest request, HttpServletResponse response,
		String email, String authorities) throws IOException {
		String accessToken = generateAccessToken(email, authorities);
		String refreshToken = generateRefreshToken(email);
		String uri = createURI(accessToken, refreshToken).toString();
		log.info(uri);
		getRedirectStrategy().sendRedirect(request, response, uri);
	}

	private void redirectJoin(HttpServletRequest request, HttpServletResponse response,
		String email, String authorities) throws IOException {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("email", email);
		jsonObject.addProperty("exist", false);

		response.setContentType("application/json");
		response.getWriter().print(jsonObject);
	}

	private String generateAccessToken(String email, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);

		String subject = email;
		Date expiration = jwtTokenService.getTokenExpiration(jwtTokenService.getAccessTokenExpirationMinutes());
		String base64EncodedSecretKey = jwtTokenService.encodeBase64SecretKey(jwtTokenService.getSecretKey());

		return jwtTokenService.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);
	}

	private String generateRefreshToken(String email) {
		String subject = email;
		Date expiration = jwtTokenService.getTokenExpiration(jwtTokenService.getRefreshTokenExpirationMinutes());
		String base64EncodedSecretKey = jwtTokenService.encodeBase64SecretKey(jwtTokenService.getSecretKey());

		return jwtTokenService.generateRefreshToken(subject, expiration, base64EncodedSecretKey);
	}

	private URI createURI(String accessToken, String refreshToken) {
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		queryParams.add("access_token", accessToken);
		queryParams.add("refresh_token", refreshToken);

		return UriComponentsBuilder
			.newInstance()
			.scheme("http")
			.host("localhost")
			.port(8080)
			.path("/")
			.queryParams(queryParams)
			.build()
			.toUri();
	}
}
