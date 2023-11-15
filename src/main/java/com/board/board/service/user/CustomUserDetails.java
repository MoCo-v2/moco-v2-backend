package com.board.board.service.user;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.board.board.domain.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
	private final User user;

	/* 유저의 권한 목록 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collectors = new ArrayList<>();
		collectors.add(() -> "ROLE_" + user.getRole());
		return collectors;
	}

	@Override
	public String getPassword() {
		return null;
	}

	/* 사용자의 id를 반환 (email) */
	@Override
	public String getUsername() {
		return user.getEmail();
	}

	/* 계정 만료 여부 true : 만료 안됨 , false : 만료 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/* 계정 잠김 여부 true : 잠기지 않음, false : 잠김 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/* 비밀번호 만료 여부 true : 만료 안됨 , false : 만료 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
