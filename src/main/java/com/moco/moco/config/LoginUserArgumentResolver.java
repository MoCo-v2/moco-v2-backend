package com.moco.moco.config;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.moco.moco.config.auth.UserInfo;
import com.moco.moco.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
	private final UserRepository userRepository;

	/**
	 * Controller의 파라미터 값을 검사하는 콜백 함수
	 * @param parameter 클라이언트로 부터 받은 파라미터
	 * @return 콜백 함수의 true/false
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {

		boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUserInfo.class) != null;
		boolean isUserClass = UserInfo.class.equals(parameter.getParameterType());

		return isLoginUserAnnotation && isUserClass;
	}

	/**
	 * supportsParameter 콜백 함수에서 true를 반환했을 경우 호출되는 콜백 함수
	 * @throws Exception
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return (UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
