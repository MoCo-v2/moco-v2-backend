package com.moco.moco.repository;

import static com.moco.moco.domain.QComment.*;
import static com.moco.moco.domain.QPost.*;
import static com.moco.moco.domain.QUser.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.moco.moco.domain.Comment;
import com.moco.moco.dto.queryDslDto.PostDetailVo;
import com.moco.moco.dto.queryDslDto.PostVo;
import com.moco.moco.dto.queryDslDto.QPostDetailVo;
import com.moco.moco.dto.queryDslDto.QPostVo;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustom {
	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	//모집중인 게시글 목록을 페이징하여 가져온다.
	public List<PostVo> getPostsOnRecruit(Pageable pageable) {
		return queryFactory
			.select(
				new QPostVo(post.id, post.title, post.content, post.type, post.capacity, post.mode, post.duration,
					post.techStack, post.recruitmentPosition, post.deadLine, post.contact_method, post.link, post.view,
					post.commentCnt, post.createdDate, post.isRemoved, post.isFull, user.id, user.name, user.picture))
			.from(post)
			.innerJoin(post.user, user)
			.on(post.user.id.eq(user.id))
			.where(post.isRemoved.eq(false).and(post.isFull.eq(false)))
			.orderBy(post.createdDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	//모든 게시글을 페이징처리하여 가져온다.
	public List<PostVo> getPosts(Pageable pageable) {
		return queryFactory
			.select(
				new QPostVo(post.id, post.title, post.content, post.type, post.capacity, post.mode, post.duration,
					post.techStack, post.recruitmentPosition, post.deadLine, post.contact_method, post.link, post.view,
					post.commentCnt, post.createdDate, post.isRemoved, post.isFull, user.id, user.name, user.picture))
			.from(post)
			.innerJoin(post.user, user)
			.on(post.user.id.eq(user.id))
			.where(post.isRemoved.eq(false))
			.orderBy(post.createdDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	//게시글 상세 정보를 가져온다.
	public Optional<PostDetailVo> getPost(Long postId) {
		Optional<PostDetailVo> postDetailVo = Optional.ofNullable(queryFactory
			.select(
				new QPostDetailVo(post.id, post.title, post.content, post.type, post.capacity, post.mode, post.duration,
					post.techStack, post.recruitmentPosition, post.deadLine, post.contact_method, post.link, post.view,
					post.commentCnt, post.createdDate, post.isRemoved, post.isFull, user.id, user.name, user.picture))
			.from(post)
			.innerJoin(post.user, user)
			.where(
				post.id.eq(postId)
					.and(post.isRemoved.eq(false))
			)
			.fetchOne());

		return postDetailVo;
	}

	//특정 게시글의 댓글을 가져온다.
	public List<Comment> getComments(Long postId) {
		return queryFactory.selectFrom(comment)
			.leftJoin(comment.post, post).fetchJoin()
			.leftJoin(comment.user, user).fetchJoin()
			.leftJoin(comment.parent).fetchJoin()
			.where(comment.post.id.eq(postId))
			.orderBy(comment.parent.id.asc().nullsFirst(), comment.createdDate.asc())
			.fetch();
	}

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
	// /* 좋아요 개수 -1 */
	// @Transactional
	// public void updateLikeCountMinus(Long postId) {
	// 	JPAUpdateClause updateClause = new JPAUpdateClause(em, post);
	// 	updateClause.where(post.id.eq(postId)).set(post.likecnt, post.likecnt.subtract(1)).execute();
	// }
}
