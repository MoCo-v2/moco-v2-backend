package com.moco.moco.jpaRepository;

import static com.moco.moco.domain.QPost.*;
import static com.moco.moco.domain.QUser.*;

import java.time.LocalDate;
import java.util.Arrays;
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

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	//게시글 상세 정보를 가져온다.
	public Optional<PostVo> getPost(Long postId) {
		PostVo postVo = queryFactory
			.select(
				new QPostVo(post.id, post.title, post.content, post.type, post.capacity, post.mode, post.duration,
					post.techStack, post.recruitmentPosition, post.deadLine, post.contactMethod, post.link, post.view,
					post.commentCnt, post.createdDate, post.isRemoved, post.isFull, user.id, user.name, user.picture))
			.from(post)
			.innerJoin(post.user, user)
			.where(
				post.id.eq(postId)
					.and(post.isRemoved.eq(false))
			)
			.fetchOne();

		if (postVo != null) {
			queryFactory.update(post)
				.set(post.view, post.view.add(1))
				.where(post.id.eq(postId))
				.execute();
		}

		return Optional.ofNullable(postVo);
	}

	//게시글을 페이징처리하여 가져온다.
	public Page<PostVo> getPosts(
		Pageable pageable,
		boolean recruit,
		String username,
		String type,
		String position,
		String mode,
		String language) {
		List<PostVo> posts = queryFactory
			.select(
				new QPostVo(post.id, post.title, post.content, post.type, post.capacity, post.mode, post.duration,
					post.techStack, post.recruitmentPosition, post.deadLine, post.contactMethod, post.link, post.view,
					post.commentCnt, post.createdDate, post.isRemoved, post.isFull, user.id, user.name, user.picture))
			.from(post)
			.innerJoin(post.user, user)
			.on(post.user.id.eq(user.id))
			.where(post.isRemoved.eq(false)
				.and(recruitEq(recruit))
				.and(usernameEq(username))
				.and(typeEq(type))
				.and(positionEq(position))
				.and(modeEq(mode))
				.and(languageEq(language))
			)
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

	//마감이 얼마남지 않은 게시글을 가져온다.
	public List<PostVo> getPostsNearDeadline() {
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusDays(14);

		return queryFactory
			.select(
				new QPostVo(post.id, post.title, post.content, post.type, post.capacity, post.mode, post.duration,
					post.techStack, post.recruitmentPosition, post.deadLine, post.contactMethod, post.link, post.view,
					post.commentCnt, post.createdDate, post.isRemoved, post.isFull, user.id, user.name, user.picture))
			.from(post)
			.innerJoin(post.user, user)
			.on(post.user.id.eq(user.id))
			.where(post.isRemoved.eq(false)
				.and(post.isFull.eq(false))
				.and(post.deadLine.between(startDate, endDate))
			)
			.orderBy(post.createdDate.desc())
			.limit(6)
			.fetch();
	}

	private BooleanExpression recruitEq(boolean recruit) {
		if (recruit) {
			return null;
		}
		return post.isFull.eq(recruit);
	}

	private BooleanExpression usernameEq(String username) {
		if (username == null) {
			return null;
		}
		return user.name.eq(username);
	}

	private BooleanExpression typeEq(String type) {
		if (type == null) {
			return null;
		}
		return post.type.eq(type);
	}

	private BooleanExpression positionEq(String position) {
		if (position == null) {
			return null;
		}
		return post.recruitmentPosition.contains(position);
	}

	private BooleanExpression modeEq(String mode) {
		if (mode == null) {
			return null;
		}
		return post.mode.eq(mode);
	}

	private BooleanExpression languageEq(String language) {
		if (language == null) {
			return null;
		}

		String[] languages = language.split(",");
		List<String> languageList = Arrays.asList(languages);

		return languageList.stream()
			.map(post.techStack::contains)
			.reduce(BooleanExpression::or)
			.orElse(null);
	}

}
