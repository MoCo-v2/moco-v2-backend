package com.board.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.board.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
