package com.moco.moco.config.auth;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class UserInfo implements Serializable {
	private Long id;
	private String email;
	private List<String> roles;
}