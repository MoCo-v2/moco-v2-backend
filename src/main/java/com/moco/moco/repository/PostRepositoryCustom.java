package com.moco.moco.repository;

import static com.moco.moco.domain.QPost.*;
import static com.moco.moco.domain.QUser.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.moco.moco.dto.PostListVo;
import com.moco.moco.dto.QPostListVo;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustom {
	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	/* 모든 게시글 리스트 가져오기 */
	public List<PostListVo> getPosts(Pageable pageable) {
		return queryFactory
			.select(
				new QPostListVo(post.id, post.title, post.content, post.type, post.capacity, post.mode, post.duration,
					post.techStack, post.recruitmentPosition, post.deadLine, post.contact_method, post.link, post.view,
					post.commentCnt, post.createdDate, user.id, user.name, user.picture))
			.from(post)
			.leftJoin(user)
			.on(post.user.id.eq(user.id))
			.orderBy(post.createdDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	// /* 모집중인 게시글 리스트 가져오기 */
	// public List<PostListVo> getPostsOnRecruit(Pageable pageable) {
	// 	return queryFactory
	// 		.select(new QPostListVo(post.id, post.createdDate, post.title, post.writer, post.content, user.id,
	// 			post.view, post.thumbnail, post.subcontent, post.likecnt, post.commentcnt, user.picture,
	// 			post.hashTag, post.isfull))
	// 		.from(post)
	// 		.leftJoin(user)
	// 		.on(post.user.id.eq(user.id))
	// 		.where(post.isfull.eq(false))
	// 		.orderBy(post.createdDate.desc())
	// 		.offset(pageable.getOffset())
	// 		.limit(pageable.getPageSize())
	// 		.fetch();
	// }
	//
	// /* 모집중인 게시글 검색 */
	// public List<PostListVo> getSearchPost(Pageable pageable, String keyword) {
	// 	return queryFactory
	// 		.select(new QPostListVo(post.id, post.createdDate, post.title, post.writer, post.content, user.id,
	// 			post.view, post.thumbnail, post.subcontent, post.likecnt, post.commentcnt, user.picture,
	// 			post.hashTag, post.isfull))
	// 		.from(post)
	// 		.leftJoin(user)
	// 		.on(post.user.id.eq(user.id))
	// 		.orderBy(post.createdDate.desc())
	// 		.where(post.isfull.eq(false), post.title.contains(keyword))
	// 		.offset(pageable.getOffset())
	// 		.limit(pageable.getPageSize())
	// 		.fetch();
	// }
	//
	// /* 내가 쓴글 가져오기 */
	// public List<PostListVo> getMyPosts(Pageable pageable, Long userId) {
	// 	return queryFactory
	// 		.select(new QPostListVo(post.id, post.createdDate, post.title, post.writer, post.content, user.id,
	// 			post.view, post.thumbnail, post.subcontent, post.likecnt, post.commentcnt, user.picture,
	// 			post.hashTag, post.isfull))
	// 		.from(post)
	// 		.leftJoin(user)
	// 		.on(post.user.id.eq(user.id))
	// 		.orderBy(post.createdDate.desc())
	// 		.where(post.user.id.eq(userId))
	// 		.offset(pageable.getOffset())
	// 		.limit(pageable.getPageSize())
	// 		.fetch();
	// }
	//
	// /* 댓글 개수 +1 */
	// @Transactional
	// public void updateCommentCountPlus(Long postId) {
	// 	JPAUpdateClause updateClause = new JPAUpdateClause(em, post);
	// 	updateClause.where(post.id.eq(postId)).set(post.commentcnt, post.commentcnt.add(1)).execute();
	// }
	//
	// /* 댓글 개수 -1 */
	// @Transactional
	// public void updateCommentCountMinus(Long postId) {
	// 	JPAUpdateClause updateClause = new JPAUpdateClause(em, post);
	// 	updateClause.where(post.id.eq(postId)).set(post.commentcnt, post.commentcnt.subtract(1)).execute();
	// }
	//
	// /* 좋아요 개수 +1 */
	// @Transactional
	// public void updateLikeCountPlus(Long postId) {
	// 	JPAUpdateClause updateClause = new JPAUpdateClause(em, post);
	// 	updateClause.where(post.id.eq(postId)).set(post.likecnt, post.likecnt.add(1)).execute();
	// }
	//
	// /* 댓글 개수 -1 */
	// @Transactional
	// public void updateLikeCountMinus(Long postId) {
	// 	JPAUpdateClause updateClause = new JPAUpdateClause(em, post);
	// 	updateClause.where(post.id.eq(postId)).set(post.likecnt, post.likecnt.subtract(1)).execute();
	// }
}
