package com.board.board.QuerydslTest;

import static com.board.board.domain.QPost.*;
import static com.board.board.domain.QUser.*;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.board.board.domain.Post;
import com.board.board.dto.PostListVo;
import com.board.board.dto.QPostListVo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@DisplayName("queryDsl 테스트 코드")
@SpringBootTest
public class QuerydslTest {

	@Autowired
	private EntityManager em;
	@Autowired
	private JPAQueryFactory queryFactory;

	private final int PAGE_POST_COUNT = 9; // 한 페이지에 존재하는 게시글 수
	private final int PageNum = 1; // 페이지
	private final Pageable pageable = PageRequest.of(PageNum - 1, PAGE_POST_COUNT,
		Sort.by(Sort.Direction.DESC, "created_date"));

	@BeforeEach
	public void 엔티티매니저_주입() {
		queryFactory = new JPAQueryFactory(em);
	}

	@DisplayName("모든 게시글 가져오기")
	@Test
	public void 모든_게시글_가져오기() {
		List<Post> boards = queryFactory.selectFrom(post)
			.fetch();

		boards.stream().forEach(board -> System.out.println(board.getCommentcnt()));
		System.out.println(boards.size());
	}

	@DisplayName("페이징 구현")
	@Test
	public void 페이징_구현하기_테스트() {
		List<PostListVo> results = queryFactory
			.select(new QPostListVo(post.id, post.createdDate, post.title, post.writer, post.content, user.id,
				post.view, post.thumbnail, post.subcontent, post.likecnt, post.commentcnt, user.picture,
				post.hashTag, post.isfull))
			.from(post)
			.leftJoin(user)
			.on(post.user.id.eq(user.id))
			.orderBy(post.createdDate.desc())
			.offset(pageable.getOffset()) //N번 부터 시작
			.limit(pageable.getPageSize())
			.fetch();

		System.out.println(results.size());
	}

	@DisplayName("검색 + 페이징")
	@Test
	public void 검색한_게시글_페이징_테스트() {
		List<PostListVo> results = queryFactory
			.select(new QPostListVo(post.id, post.createdDate, post.title, post.writer, post.content, user.id,
				post.view, post.thumbnail, post.subcontent, post.likecnt, post.commentcnt, user.picture,
				post.hashTag, post.isfull))
			.from(post)
			.leftJoin(user)
			.on(post.user.id.eq(user.id))
			.orderBy(post.createdDate.desc())
			.where(post.isfull.eq(false), post.title.contains("a"))
			.offset(pageable.getOffset()) //N번 부터 시작
			.limit(pageable.getPageSize())
			.fetch();

		System.out.println(results.size());
	}

	@DisplayName("댓글 개수, 좋아요 개수 테스트")
	@Test
	@Transactional
	public void 업데이트_테스트() {
		JPAUpdateClause updateClause = new JPAUpdateClause(em, post);
		updateClause.where(post.id.eq(1L)).set(post.commentcnt, post.commentcnt.add(1)).execute();
		int count = queryFactory.selectFrom(post).where(post.id.eq(1L)).fetchOne().getCommentcnt();
		Assert.assertEquals(1, count);
	}
}
