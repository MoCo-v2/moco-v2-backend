package com.board.board.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomAuthenticationException extends RuntimeException {
	ErrorCode errorCode;
}
