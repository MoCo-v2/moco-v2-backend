package com.moco.moco.config.argsResolver;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfo implements Serializable {
	private String id;
	private List<String> roles;
}