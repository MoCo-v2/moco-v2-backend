package com.board.board.service.user;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.board.board.config.auth.SessionUser;
import com.board.board.domain.User;
import com.board.board.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;
	private final HttpSession httpSession;

	/* 로그인 과정 진입시 해당 로직(loadUserByUsername)을 거침 */
	@Override
	public UserDetails loadUserByUsername(String email) throws AuthenticationException {
		User user = userRepository.findByEmail(email).orElseThrow(() ->
			new UsernameNotFoundException("해당 사용자가 존재하지 않습니다. : " + email));

		httpSession.setAttribute("user", new SessionUser(user));

		return new CustomUserDetails(user);
	}

}
