package com.board.board.service.user;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.board.board.config.auth.SessionUser;
import com.board.board.domain.User;
import com.board.board.dto.OAuthAttributes;
import com.board.board.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserRepository userRepository;
	private final HttpSession httpSession;

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

		OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
			oAuth2User.getAttributes());

		int idx = 0;
		/* 임시 아이디 부여 */
		while (true) {
			if (userRepository.existsByName("열공하는라이언" + Integer.toString(idx))) {
				idx++;
			} else {
				attributes.setName("열공하는라이언" + Integer.toString(idx));
				break;
			}
		}

		/* attributes <= 로그인 성공후 유저 데이터가 담긴 상태  */
		User user = saveOrUpdate(attributes);
		/* 세션 저장 */
		httpSession.setAttribute("user", new SessionUser(user));

		return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
			attributes.getAttributes(),
			attributes.getNameAttributeKey());
	}

	/* 소셜로그인시 기존 회원이 존재하면 수정날짜 정보만 업데이트하여 기존의 데이터는 그대로 보존 */
	private User saveOrUpdate(OAuthAttributes attributes) {

		User user = userRepository.findByEmail(attributes.getEmail()).orElse(attributes.toEntity());
		return userRepository.save(user);
	}

}
