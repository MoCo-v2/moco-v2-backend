package com.moco.moco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moco.moco.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

}
