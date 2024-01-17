package com.moco.moco.common;

import java.util.regex.Pattern;

import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;

public class Validation {

	public static void validationUserId(String userId) {
		if (userId.isBlank() || !Pattern.matches("^(google|kakao|github).*", userId)) {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}
	}

	public static void validationPostId(Long postId) {
		if (postId == 0 || postId < 0) {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}
	}

	public static void validationUserIdAndPostId(String userId, Long postId) {
		validationUserId(userId);
		validationPostId(postId);
	}

	public static void validationCommentId(Long commentId) {
		if (commentId == 0 || commentId < 0) {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}
	}

	public static void validationBannertId(Long bannerId) {
		if (bannerId == 0 || bannerId < 0) {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}
	}
}
