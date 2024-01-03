package com.moco.moco.repository;

import static com.moco.moco.domain.QComment.*;
import static com.moco.moco.domain.QPost.*;
import static com.moco.moco.domain.QUser.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.moco.moco.domain.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustom {
	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	//특정 게시글의 댓글을 가져온다.
	public List<Comment> getComments(Long postId) {
		return queryFactory.selectFrom(comment)
			.leftJoin(comment.post, post).fetchJoin()
			.leftJoin(comment.user, user).fetchJoin()
			.leftJoin(comment.parent).fetchJoin()
			.where(comment.post.id.eq(postId).and(comment.isRemoved.eq(false)))
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
