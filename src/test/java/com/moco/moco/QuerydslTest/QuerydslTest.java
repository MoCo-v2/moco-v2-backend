package com.moco.moco.QuerydslTest;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.moco.moco.domain.Post;
import com.moco.moco.domain.QPost;
import com.moco.moco.domain.QUser;
import com.moco.moco.dto.PostListVo;
import com.moco.moco.dto.QPostListVo;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

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
		List<Post> boards = queryFactory.selectFrom(QPost.post)
			.fetch();

		boards.stream().forEach(board -> System.out.println(board.getCommentcnt()));
		System.out.println(boards.size());
	}

	@DisplayName("페이징 구현")
	@Test
	public void 페이징_구현하기_테스트() {
		List<PostListVo> results = queryFactory
			.select(new QPostListVo(
				QPost.post.id, QPost.post.createdDate, QPost.post.title, QPost.post.writer, QPost.post.content,
				QUser.user.id,
				QPost.post.view, QPost.post.thumbnail, QPost.post.subcontent, QPost.post.likecnt, QPost.post.commentcnt,
				QUser.user.picture,
				QPost.post.hashTag, QPost.post.isfull))
			.from(QPost.post)
			.leftJoin(QUser.user)
			.on(QPost.post.user.id.eq(QUser.user.id))
			.orderBy(QPost.post.createdDate.desc())
			.offset(pageable.getOffset()) //N번 부터 시작
			.limit(pageable.getPageSize())
			.fetch();

		System.out.println(results.size());
	}

	@DisplayName("검색 + 페이징")
	@Test
	public void 검색한_게시글_페이징_테스트() {
		List<PostListVo> results = queryFactory
			.select(new QPostListVo(
				QPost.post.id, QPost.post.createdDate, QPost.post.title, QPost.post.writer, QPost.post.content,
				QUser.user.id,
				QPost.post.view, QPost.post.thumbnail, QPost.post.subcontent, QPost.post.likecnt, QPost.post.commentcnt,
				QUser.user.picture,
				QPost.post.hashTag, QPost.post.isfull))
			.from(QPost.post)
			.leftJoin(QUser.user)
			.on(QPost.post.user.id.eq(QUser.user.id))
			.orderBy(QPost.post.createdDate.desc())
			.where(QPost.post.isfull.eq(false), QPost.post.title.contains("a"))
			.offset(pageable.getOffset()) //N번 부터 시작
			.limit(pageable.getPageSize())
			.fetch();

		System.out.println(results.size());
	}
}
