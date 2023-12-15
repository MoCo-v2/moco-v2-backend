package com.moco.moco.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.moco.moco.config.auth.UserInfo;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.service.jwt.JwTokenService;

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
			UserInfo userInfo = getTokenInHeaderAndVerify(request);
			setAuthenticationToContext(userInfo);
		} catch (SignatureException se) {
			log.info("검증되지 않은 토큰으로 요청", JwtVerificationFilter.class);
			request.setAttribute(EXCEPTION_KET, ErrorCode.INVALID_REQUEST.getMessage());
		} catch (ExpiredJwtException ee) {
			log.info("만료된 토큰으로 요청", JwtVerificationFilter.class);
			request.setAttribute(EXCEPTION_KET, ErrorCode.INVALID_AUTH_TOKEN.getMessage());
		} catch (Exception e) {
			log.info("필터 예외 발생", JwtVerificationFilter.class);
			log.info(e.getMessage());
			request.setAttribute(EXCEPTION_KET, ErrorCode.INVALID_AUTH_TOKEN.getMessage());
		}
		filterChain.doFilter(request, response);
	}

	// 필터를 통과시킬 url 패턴
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String[] excludePath = {"/api/v1/public/"};
		String path = request.getRequestURI();
		return Arrays.stream(excludePath).anyMatch(path::startsWith);
	}

	// jwt 토큰을 검증한다.
	private UserInfo getTokenInHeaderAndVerify(HttpServletRequest request) {
		String jws = request.getHeader("Authorization").replace("Bearer ", "");

		String secretKey = jwtTokenService.encodeBase64SecretKey(jwtTokenService.getSecretKey());

		return jwtTokenService.verifySignature(jws, secretKey);
	}

	// 인증객체에 정보를 토큰의 정보를 담는다.
	private void setAuthenticationToContext(UserInfo userInfo) {
		List<GrantedAuthority> authorities = userInfo.getRoles().stream()
			.map(role -> new SimpleGrantedAuthority(role))
			.collect(Collectors.toList());

		Authentication authentication = new UsernamePasswordAuthenticationToken(userInfo, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}

