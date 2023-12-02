package com.moco.moco.domain;

import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum OauthType {
	GOOGLE,
	KAKAO,
	GITHUB;

	// 대문자로 치환하여 Oauth 타입을 검사한다.
	// 존재하지 않는 타입을 요청한 경우 커스텀 예외를 발생시킨다.
	@JsonCreator
	public static OauthType parsing(String provider) {
		try {
			return OauthType.valueOf(provider.toUpperCase());
		} catch (Exception e) {
			throw new CustomAuthenticationException(ErrorCode.INVALID_REQUEST);
		}
	}
}
