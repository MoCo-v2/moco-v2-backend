package com.moco.moco.jpaRepository;

import static com.moco.moco.domain.QBookmark.*;
import static com.moco.moco.domain.QPost.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	// 북마크 존재 여부를 확인한다.
	public boolean isBookmarkExists(String userId, Long postId) {
		return queryFactory
			.select(bookmark)
			.from(bookmark)
			.where(bookmark.user.id.eq(userId)
				.and(bookmark.post.id.eq(postId)))
			.fetchFirst() != null;
	}

	// 내 북마크 id 리스트를 가져온다.
	public List<Long> getMyBookmarks(String userId) {
		return queryFactory.select(bookmark.post.id)
			.from(bookmark)
			.where(bookmark.user.id.eq(userId))
			.fetch();
	}

	// 삭제된 게시글을 북마크에서 제거한다.
	public void deleteBookmarkedPost(Long deletedPostId) {
		queryFactory.delete(bookmark).where(post.id.eq(deletedPostId)).execute();
	}
}
