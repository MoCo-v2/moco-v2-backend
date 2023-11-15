package com.board.board.config.auth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class OauthSuccessHandler implements AuthenticationSuccessHandler {
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		/* 로그인 성공후 session에 저장된 이름 get */
		HttpSession session = request.getSession();
		SessionUser sessionUser = (SessionUser)session.getAttribute("user");

		/* 이름 중복체크에 따라 분기 */
		if (sessionUser.isNameCheck()) {
			response.sendRedirect("/");
		} else {
			response.sendRedirect("/OauthNameCheck");
		}
	}
}
