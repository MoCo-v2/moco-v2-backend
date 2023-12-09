package com.moco.moco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moco.moco.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

	boolean existsByName(String name);

	/* user GET */
	User findByName(String name);

}