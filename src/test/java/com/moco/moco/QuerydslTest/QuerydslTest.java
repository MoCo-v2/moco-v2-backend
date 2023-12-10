package com.moco.moco.QuerydslTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.moco.moco.repository.PostRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@DisplayName("queryDsl 테스트 코드")
@SpringBootTest
public class QuerydslTest {

	@Autowired
	private EntityManager em;
	@Autowired
	private JPAQueryFactory queryFactory;
	@Autowired
	private PostRepositoryCustom postRepositoryCustom;

	@BeforeEach
	public void 엔티티매니저_주입() {
		queryFactory = new JPAQueryFactory(em);
	}

}
