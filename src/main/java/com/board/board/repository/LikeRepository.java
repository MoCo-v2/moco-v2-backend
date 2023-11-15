package com.board.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.board.domain.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
	/* likeGet - exist */
	boolean existsByUser_IdAndBoard_Id(Long user_id, Long board_id);

	/* likeGet - find */
	Like findByUser_IdAndBoard_Id(Long user_id, Long board_id);

	/* likeSize - count */
	Long countByBoard_Id(Long board_id);
}
