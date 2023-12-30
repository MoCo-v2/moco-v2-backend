package com.moco.moco.exception;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

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

		JsonObject responseJson = new JsonObject();
		responseJson.addProperty("code", String.valueOf(errorCode.getHttpStatus()));
		responseJson.addProperty("message", errorCode.getMessage());

		response.getWriter().write(responseJson.toString());
	}

}