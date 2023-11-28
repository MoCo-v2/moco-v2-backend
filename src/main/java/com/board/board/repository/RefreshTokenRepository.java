package com.board.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.board.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByEmail(String email);
}
