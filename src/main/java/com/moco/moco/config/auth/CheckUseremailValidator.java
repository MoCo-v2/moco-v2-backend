package com.moco.moco.config.auth;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.moco.moco.dto.UserDto;
import com.moco.moco.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CheckUseremailValidator extends AbstractValidator<UserDto.Request> {
	private final UserRepository userRepository;

	@Override
	protected void doValidate(UserDto.Request userDto, Errors errors) {
		if (userRepository.existsByEmail(userDto.toEntity().getEmail())) {
			errors.rejectValue("email", "아이디 중복 오류", "이미 사용중인 아이디입니다.");
		}
	}

}
