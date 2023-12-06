package com.moco.moco.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moco.moco.domain.User;

public interface UserRepository extends JpaRepository<User, String> {
	/* Security */
	Optional<User> findByEmail(String email);

	/* 회원가입 이메일 중복체크 */
	boolean existsByEmail(String email);

	boolean existsByName(String name);

	/* user GET */
	User findByName(String name);

}