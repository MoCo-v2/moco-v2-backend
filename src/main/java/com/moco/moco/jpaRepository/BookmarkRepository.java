package com.moco.moco.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moco.moco.domain.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

	Bookmark findByUserIdAndPostId(String userId, Long postId);

}
