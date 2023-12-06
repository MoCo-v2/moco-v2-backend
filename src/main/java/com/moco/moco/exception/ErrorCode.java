package com.moco.moco.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	// 400 BAD_REQUEST : 잘못된 요청
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	INVALID_REQUEST(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 요청입니다."),

	// 401 UNAUTHORIZED : 인증되지 않은 사용자
	INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "권한 정보가 없는 토큰입니다."),

	// 404 NOT_FOUND : Resource를 찾을 수 없음
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

	// 409 : CONFLICT : Resource의 현재 상태와 충돌. 보통 중복된 데이터 존재
	DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "데이터가 이미 존재합니다."),
	;

	private final HttpStatus httpStatus;
	private final String message;
}
