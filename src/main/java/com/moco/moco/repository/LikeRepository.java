package com.moco.moco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moco.moco.domain.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
	/* likeGet - exist */
	boolean existsByUser_IdAndPost_Id(String user_id, Long post_id);

	/* likeGet - find */
	Like findByUser_IdAndPost_Id(String user_id, Long post_id);

	/* likeSize - count */
	Long countByPost_Id(Long post_id);
}
