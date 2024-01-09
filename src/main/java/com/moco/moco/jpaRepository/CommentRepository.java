package com.moco.moco.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moco.moco.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
