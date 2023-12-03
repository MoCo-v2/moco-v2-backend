package com.moco.moco.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomAuthenticationException extends RuntimeException {
	ErrorCode errorCode;
}
