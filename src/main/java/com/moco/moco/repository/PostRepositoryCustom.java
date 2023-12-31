package com.moco.moco.repository;

import static com.moco.moco.domain.QPost.*;
import static com.moco.moco.domain.QUser.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.moco.moco.dto.queryDslDto.PostVo;
import com.moco.moco.dto.queryDslDto.QPostVo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustom {
	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	//게시글 상세 정보를 가져온다.
	public Optional<PostVo> getPost(Long postId) {
		PostVo postVo = queryFactory
			.select(
				new QPostVo(post.id, post.title, post.content, post.type, post.capacity, post.mode, post.duration,
					post.techStack, post.recruitmentPosition, post.deadLine, post.contactMethod, post.link, post.view,
					post.commentCnt, post.createdDate, post.isRemoved, post.isFull, user.name, user.picture))
			.from(post)
			.innerJoin(post.user, user)
			.where(
				post.id.eq(postId)
					.and(post.isRemoved.eq(false))
			)
			.fetchOne();

		if (post != null) {
			queryFactory.update(post)
				.set(post.view, post.view.add(1))
				.where(post.id.eq(postId))
				.execute();
		}

		return Optional.ofNullable(postVo);
	}

	//게시글을 페이징처리하여 가져온다.
	public Page<PostVo> getPosts(Pageable pageable, boolean recruit, String username) {
		List<PostVo> posts = queryFactory
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

		JPAQuery<Long> countQuery = queryFactory.select(post.count())
			.from(post)
			.innerJoin(post.user, user)
			.on(post.user.id.eq(user.id))
			.where(post.isRemoved.eq(false)
				.and(post.isFull.eq(recruit))
				.and(usernameEq(username)));

		return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchOne);

	}

	private BooleanExpression usernameEq(String username) {
		if (username == null) {
			return null;
		}
		return user.name.eq(username);
	}
}
