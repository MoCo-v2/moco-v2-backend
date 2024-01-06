package com.moco.moco.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moco.moco.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
	Optional<Post> findByIdAndIsRemoved(Long id, boolean isRemoved);
}
