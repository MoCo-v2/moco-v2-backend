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
	INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다. 재발급을 받으세요."),
	UNAUTHORIZED_WRITER(HttpStatus.UNAUTHORIZED, "작성자가 아닙니다."),

	// 403 FORBIDDEN : 권한이 없는 요청
	NEED_LOGIN(HttpStatus.FORBIDDEN, "로그인을 해주세요"),

	// 404 NOT_FOUND : Resource를 찾을 수 없음
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
	BANNER_NOT_FOUNT(HttpStatus.NOT_FOUND, "배너를 찾을 수 없습니다."),

	// 409 : CONFLICT : Resource의 현재 상태와 충돌. 보통 중복된 데이터 존재
	DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "데이터가 이미 존재합니다."),

	// 500 : INTERNAL SERVER ERROR
	SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "요청을 처리하지 못했습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
