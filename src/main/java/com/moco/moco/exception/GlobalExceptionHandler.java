package com.moco.moco.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomAuthenticationException.class)
	protected ResponseEntity<ErrorResponse> handleAuthException(CustomAuthenticationException e) {
		return ErrorResponse.toResponseEntity(e.getErrorCode().getHttpStatus(), e.getErrorCode().getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> methodArgNotValidException(MethodArgumentNotValidException e) {
		return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다.");
	}

	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<ErrorResponse> unexpectedRuntimeException(RuntimeException e) {
		return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
	}
}
