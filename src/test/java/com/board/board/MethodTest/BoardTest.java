package com.board.board.MethodTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.board.board.domain.Post;
import com.board.board.repository.PostRepository;
import com.board.board.repository.UserRepository;

@DisplayName("JPA 테스트")
@SpringBootTest
public class BoardTest {

	@Autowired
	private PostRepository boardRepository;
	@Autowired
	private UserRepository userRepository;

	private static final int pageNum = 1;
	private static final int PAGE_POST_COUNT = 9;

	@Test
	public void 게시글_가져오기() {
		PageRequest pageRequest = PageRequest.of(pageNum - 1, PAGE_POST_COUNT,
			Sort.by(Sort.Direction.DESC, "createdDate"));
		//List<BoardDto.Response> boardList = boardRepository.findAll(pageRequest).stream().map(board -> new BoardDto.Response(board)).collect(Collectors.toList());
		Page<Post> boards = boardRepository.findAll(pageRequest);

	}
}
