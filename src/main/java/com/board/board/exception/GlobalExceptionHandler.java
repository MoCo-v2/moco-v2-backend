package com.board.board.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomAuthenticationException.class)
	protected ResponseEntity<ErrorResponseEntity> handleAuthException(CustomAuthenticationException e) {
		return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
	}
}
