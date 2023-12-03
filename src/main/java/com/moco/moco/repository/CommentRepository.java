package com.moco.moco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moco.moco.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
