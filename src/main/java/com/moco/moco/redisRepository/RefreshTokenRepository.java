package com.moco.moco.redisRepository;

import org.springframework.data.repository.CrudRepository;

import com.moco.moco.domain.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
