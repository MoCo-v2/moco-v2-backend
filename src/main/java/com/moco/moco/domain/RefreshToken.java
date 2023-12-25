package com.moco.moco.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

@Getter
@RedisHash(value = "jwtToken", timeToLive = 60 * 60 * 24 * 3)
public class RefreshToken {

	@Id
	private String refreshToken;
	private String userId;

	public RefreshToken(String refreshToken, String userId) {
		this.refreshToken = refreshToken;
		this.userId = userId;
	}
}