package com.moco.moco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moco.moco.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
