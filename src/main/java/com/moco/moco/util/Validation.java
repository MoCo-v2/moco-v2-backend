package com.moco.moco.util;

import java.util.regex.Pattern;

import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;

public class Validation {

	public void validationUserId(String userId) {
		if (userId.isBlank() || !Pattern.matches("^(google|kakao|github).*", userId)) {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}
	}

	public void validationPostId(Long postId) {
		if (postId == 0 || postId < 0) {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}
	}

	public void validationCommentId(Long commentId) {
		if (commentId == 0 || commentId < 0) {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}
	}
}
