package com.moco.moco.repository;

import org.springframework.data.repository.CrudRepository;

import com.moco.moco.domain.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
