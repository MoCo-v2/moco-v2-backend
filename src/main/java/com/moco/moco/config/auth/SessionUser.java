package com.moco.moco.config.auth;

import java.io.Serializable;

import com.moco.moco.domain.User;

import lombok.Getter;

@Getter
public class SessionUser implements Serializable {
	private Long id;
	private String name;
	private String email;
	private String picture;
	private boolean nameCheck;

	public SessionUser(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.picture = user.getPicture();
	}
}