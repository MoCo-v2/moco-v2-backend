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
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustom {
	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	//게시글을 페이징처리하여 가져온다.
	public List<PostVo> getPosts(Pageable pageable, boolean recruit, String username) {
		return queryFactory
			.select(
				new QPostVo(post.id, post.title, post.content, post.type, post.capacity, post.mode, post.duration,
					post.techStack, post.recruitmentPosition, post.deadLine, post.contactMethod, post.link, post.view,
					post.commentCnt, post.createdDate, post.isRemoved, post.isFull, user.name, user.picture))
			.from(post)
			.innerJoin(post.user, user)
			.on(post.user.id.eq(user.id))
			.where(post.isRemoved.eq(false)
				.and(post.isFull.eq(recruit))
				.and(usernameEq(username)))
			.orderBy(post.createdDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	private BooleanExpression usernameEq(String username) {
		if (username == null) {
			return null;
		}
		return user.name.eq(username);
	}

	//게시글 상세 정보를 가져온다.
	public Optional<PostDetailVo> getPost(Long postId) {
		PostDetailVo postDetailVo = queryFactory
			.select(
				new QPostDetailVo(post.id, post.title, post.content, post.type, post.capacity, post.mode, post.duration,
					post.techStack, post.recruitmentPosition, post.deadLine, post.contactMethod, post.link, post.view,
					post.commentCnt, post.createdDate, post.isRemoved, post.isFull, user.name, user.picture))
			.from(post)
			.innerJoin(post.user, user)
			.where(
				post.id.eq(postId)
					.and(post.isRemoved.eq(false))
			)
			.fetchOne();

		if (postDetailVo != null) {
			queryFactory.update(post)
				.set(post.view, post.view.add(1))
				.where(post.id.eq(postId))
				.execute();
		}

		return Optional.ofNullable(postDetailVo);
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

	//게시글의 댓글 개수 +1 or -1
	public void addCommentCount(Long postId, int num) {
		queryFactory.update(post)
			.set(post.commentCnt, post.commentCnt.add(num))
			.where(post.id.eq(postId))
			.execute();
	}
}
