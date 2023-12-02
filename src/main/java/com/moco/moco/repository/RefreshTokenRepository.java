package com.moco.moco.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moco.moco.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByEmail(String email);
}
