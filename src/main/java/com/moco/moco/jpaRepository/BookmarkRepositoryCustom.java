package com.moco.moco.jpaRepository;

import static com.moco.moco.domain.QBookmark.*;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	//북마크 존재 여부를 확인한다.
	public boolean isBookmarkExists(String userId, Long postId) {
		return queryFactory
			.select(bookmark)
			.from(bookmark)
			.where(bookmark.user.id.eq(userId)
				.and(bookmark.post.id.eq(postId)))
			.fetchFirst() != null;
	}
}
