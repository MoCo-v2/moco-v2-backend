package com.moco.moco.service.user;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.moco.moco.domain.Role;
import com.moco.moco.domain.User;
import com.moco.moco.dto.OAuth2Attributes;
import com.moco.moco.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		/* OAuth2 서비스 id (구글, 카카오, 네이버, 깃허브) */
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		/* OAuth2 로그인 진행 시 키가 되는 필드 값(PK) */
		String userNameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint()
			.getUserNameAttributeName();

		OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, userNameAttributeName,
			oAuth2User.getAttributes());

		Map<String, Object> userAttribute = attributes.convertToMap();

		/* 새로운 회원인지 체크 -> 회원가입 로직 필요 */
		Optional<User> user = userRepository.findByEmail(attributes.getEmail());

		if (user.isEmpty()) {
			userAttribute.put("exist", false);
		} else {
			userAttribute.put("exist", true);
		}

		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(Role.USER.toString())),
			userAttribute,
			"email");
	}
}
