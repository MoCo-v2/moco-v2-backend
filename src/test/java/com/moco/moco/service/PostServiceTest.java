package com.moco.moco.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.moco.moco.domain.Comment;
import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;
import com.moco.moco.dto.PostDto;
import com.moco.moco.dto.queryDslDto.PostDetailVo;
import com.moco.moco.dto.queryDslDto.PostVo;
import com.moco.moco.repository.PostRepository;
import com.moco.moco.repository.PostRepositoryCustom;
import com.moco.moco.repository.UserRepository;
import com.moco.moco.service.post.PostService;

public class PostServiceTest {

	@InjectMocks
	PostService postService;

	@Mock
	PostRepositoryCustom postRepositoryCustom;
	@Mock
	PostRepository postRepository;
	@Mock
	UserRepository userRepository;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	public PostDetailVo generatePostDetailVo() {
		PostDetailVo postDetailVo = new PostDetailVo(1L,
			"프론트엔드 1분 모집합니다.",
			"모코 프로젝트 런칭하기 위해 프론트엔드 1분 모집합니다.",
			"프로젝트",
			"1명",
			"online",
			"1개월",
			"['java','react','spring']",
			"프론트엔드",
			LocalDate.now(),
			"카카오톡 오픈 채팅방",
			"www.moco.run",
			1,
			0,
			LocalDateTime.now(),
			false,
			false
			, "test_user",
			"www.moco.run/picture"
		);

		return postDetailVo;
	}

	private List<Comment> generateComments() {
		List<Comment> comments = new ArrayList<>();
		Comment comment = Comment.builder().post(mock(Post.class)).id(1L).user(mock(User.class)).build();
		comments.add(comment);
		return comments;
	}

	@Test
	public void getPostsTest() {
		// Given
		Integer offset = 0;
		Integer limit = 10;
		String recruit = "true";
		String username = "test_user";

		List<PostVo> posts = generatePostVos();
		Long total = 20L; // Replace with the actual total count

		given(postRepositoryCustom.getPosts(PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "created_date")),
			true, username))
			.willReturn(posts);
		given(postService.getPostCount()).willReturn(total);

		// When
		PostDto.Response result = postService.getPosts(offset, limit, recruit, username);

		// Then
		assertThat(result.getPosts()).isEqualTo(posts);
		assertThat(result.getTotal()).isEqualTo(total);
	}

	@Test
	public void getPostCountTest() {
		// Given
		Long expectedCount = 30L;
		given(postRepository.count()).willReturn(expectedCount);

		// When
		Long result = postService.getPostCount();

		// Then
		assertThat(result).isEqualTo(expectedCount);
	}

	private List<PostVo> generatePostVos() {
		return new ArrayList<>();
	}
}
