package com.board.board.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.board.board.exception.ErrorCode;
import com.board.board.service.token.JwTokenService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GenericFilterBean은 기존 Filter에서 얻어올 수 없었던 Spring 진영의 설정 정보를 가져올 수 있게 확장된 추상 클래스이다.
 *
 * Filter 와 GenneicFilterBean은 매 서블릿 마다 호출이 된다.
 * 서블릿은 사용자의 요청을 받으면 서블릿을 메모리에 저장해두고, 같은 클라이언트의 요청을 받으면 서블릿 객체를 재활용하여 요청을 처리한다.
 *
 * 이 서블릿이 RequestDispacter클래스에 의해 다른 서블릿으로 dispatch되는 경우가 존재하고,
 * 이 경우에 우리가 정의해둔 Filter나 GenericFilterBean으로 구현된 Filter를 또 타면서 Filter가 "두 번"실행되는 현상이 발생할 수 있다.
 * 이런 문제를 해결하기 위해 등장한 것이 "OncePerRequestFilter" 이다.
 *
 * OncePerRequestFilter는 사용자의 요청 당 딱 한번만 실행되는 Filter를 구현할 수 있다.
 */

@Slf4j
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {
	private final JwTokenService jwtTokenService;
	private final String EXCEPTION_KET = "exception";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			Map<String, Object> claims = getTokenInHeaderAndVerify(request);
			setAuthenticationToContext(claims);
		} catch (SignatureException se) {
			log.info("검증되지 않은 토큰으로 요청", JwtVerificationFilter.class);
			request.setAttribute(EXCEPTION_KET, ErrorCode.INVALID_REQUEST);
		} catch (ExpiredJwtException ee) {
			log.info("만료된 토큰으로 요청", JwtVerificationFilter.class);
			request.setAttribute(EXCEPTION_KET, ErrorCode.INVALID_AUTH_TOKEN);
		} catch (Exception e) {
			log.info("필터 예외 발생", JwtVerificationFilter.class);
			request.setAttribute(EXCEPTION_KET, ErrorCode.INVALID_AUTH_TOKEN);
		}
		filterChain.doFilter(request, response);
	}

	// 필터를 통과시킬 url 패턴
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String[] excludePath = {"/api/v1/login"};
		String path = request.getRequestURI();
		return Arrays.stream(excludePath).anyMatch(path::startsWith);
	}

	// jwt 토큰을 검증한다.
	private Map<String, Object> getTokenInHeaderAndVerify(HttpServletRequest request) {
		String jws = request.getHeader("Authorization").replace("Bearer ", "");

		String secretKey = jwtTokenService.encodeBase64SecretKey(jwtTokenService.getSecretKey());

		Map<String, Object> claims = jwtTokenService.verifySignature(jws, secretKey);

		return claims;
	}

	// 인증객체에 정보를 토큰의 정보를 담는다.
	private void setAuthenticationToContext(Map<String, Object> claims) {
		String email = (String)claims.get("email");
		List<GrantedAuthority> authorities = (List)claims.get("role");
		Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}

