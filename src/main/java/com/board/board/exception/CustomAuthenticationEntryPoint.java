package com.board.board.exception;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final String EXCEPTION_KET = "exception";

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		String exception = (String)request.getAttribute(EXCEPTION_KET);

		if (exception == null || exception.equals(ErrorCode.INVALID_AUTH_TOKEN)) {
			setResponse(response, ErrorCode.INVALID_REQUEST);
		}

		setResponse(response, ErrorCode.INVALID_AUTH_TOKEN);

	}

	public static void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		JSONObject responseJson = new JSONObject();
		responseJson.put("code", errorCode.getHttpStatus());
		responseJson.put("message", errorCode.getMessage());

		response.getWriter().write(responseJson.toJSONString());
	}

}