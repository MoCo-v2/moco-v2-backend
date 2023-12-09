package com.moco.moco.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moco.moco.domain.Post;

/* JpaRepository<Entity 클래스, PK 타입> */
public interface PostRepository extends JpaRepository<Post, Long> {

	/* 전체 게시글 리스트 가져오기 - 내가 쓴글 */
	@Query(value = "select * from Post a WHERE a.user_id = :userId", nativeQuery = true)
	List<Post> findMyPostList(Pageable pageable, @Param("userId") Long userId);

	/* 게시글 상세보기 [댓글로 인한 FetchJoin] */
	@Query(value = "select b from Post b left join fetch b.comments where b.id = :postId")
	Post findByIdWithFetchJoin(@Param("postId") Long postId);

	/* 조회수 */
	@Modifying
	@Query("update Post b set b.view = b.view + 1 where b.id = :id")
	int updateView(@Param("id") Long id);

}
