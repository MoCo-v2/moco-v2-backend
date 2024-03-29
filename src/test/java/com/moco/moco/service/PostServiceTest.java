package com.moco.moco.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;
import com.moco.moco.dto.PostDto;
import com.moco.moco.dto.queryDslDto.PostVo;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.jpaRepository.BookmarkRepositoryCustom;
import com.moco.moco.jpaRepository.CommentRepository;
import com.moco.moco.jpaRepository.PostRepository;
import com.moco.moco.jpaRepository.PostRepositoryCustom;
import com.moco.moco.jpaRepository.UserRepository;
import com.moco.moco.service.post.PostService;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

	@InjectMocks
	PostService service;
	@Mock
	PostRepositoryCustom postRepositoryCustom;
	@Mock
	PostRepository postRepository;
	@Mock
	UserRepository userRepository;
	@Mock
	BookmarkRepositoryCustom bookmarkRepositoryCustom;
	@Mock
	CommentRepository commentRepository;

	User user;
	Post post;

	private PostVo generatePostVo() {
		PostVo post = new PostVo();
		post.setId(1L);
		post.setTitle("글 제목");
		post.setContent("글 내용");
		post.setType("프로젝트");
		post.setMode("online");
		post.setDuration("3개월");
		post.setTechStack("['java','react','spring']");
		post.setRecruitmentPosition("프론트엔드");
		post.setCapacity("2명");
		post.setContactMethod("kakao talk");
		post.setLink("https://moco.run");
		post.setView(0);
		post.setCommentCnt(0);
		post.setRemoved(false);
		post.setFull(false);
		post.setWriter("MoCo");
		post.setPicture("www.google.com/picture");
		post.setDeadLine(LocalDate.now());
		post.setCreatedDate(LocalDateTime.now());
		return post;
	}

	private PostDto.Request generatePostDto() {
		PostDto.Request post = new PostDto.Request();
		post.setTitle("글 제목");
		post.setContent("글 내용");
		post.setType("프로젝트");
		post.setMode("online");
		post.setDuration("3개월");
		post.setTechStack("['java','react','spring']");
		post.setRecruitmentPosition("프론트엔드");
		post.setCapacity("2명");
		post.setContactMethod("kakao talk");
		post.setLink("https://moco.run");
		post.setDeadLine(LocalDate.now());
		return post;
	}

	private void generateUserAndPost() {
		user = User.builder()
			.id("google1234")
			.build();

		post = Post.builder()
			.id(1L)
			.user(user)
			.build();
	}

	@Test
	public void getPostTest_게시글_조회_성공() {
		//given
		PostVo post = generatePostVo();
		given(postRepositoryCustom.getPost(1L)).willReturn(Optional.ofNullable(post));

		//when
		PostVo result = service.getPost(1L);

		//then
		assertThat(result).isEqualTo(post);
	}

	@Test
	public void getPostTest_게시글_조회_실패() {
		//given
		given(postRepositoryCustom.getPost(999L)).willThrow(
			new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		//when
		assertThatThrownBy(() -> service.getPost(999L))

			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception ->
				assertThat(((CustomAuthenticationException)exception).getErrorCode()).isEqualTo(
					ErrorCode.POST_NOT_FOUND)
			);
	}

	@Test
	public void getPostsTest_게시글_페이징_조회_성공() {
		//given
		PostVo post = generatePostVo();
		Page<PostVo> expectedPage = new PageImpl<>(Collections.singletonList(post));
		given(
			postRepositoryCustom.getPosts(any(), anyBoolean(), anyString(), anyString(), anyString(), anyString(),
				anyString())).willReturn(expectedPage);

		//when
		Page<PostVo> resultPage = postRepositoryCustom.getPosts(any(), anyBoolean(), anyString(), anyString(),
			anyString(), anyString(), anyString());

		//then
		assertThat(resultPage).isEqualTo(expectedPage);
		verify(postRepositoryCustom).getPosts(any(), anyBoolean(), anyString(), anyString(), anyString(),
			anyString(), anyString());
	}

	@Test
	public void getPostsTest_게시글_페이징_조회_실패() {
		//given
		given(
			postRepositoryCustom.getPosts(any(), anyBoolean(), anyString(), anyString(), anyString(), anyString(),
				anyString())).willReturn(null);

		//when
		Page<PostVo> resultPage = postRepositoryCustom.getPosts(any(), anyBoolean(), anyString(), anyString(),
			anyString(), anyString(), anyString());

		//then
		assertThat(resultPage).isNull();
	}

	@Test
	public void getMyBookmarkPosts_나의_북마크_게시글_페이징_조회_성공() {
		//given
		PostVo post = generatePostVo();
		Page<PostVo> expectedPage = new PageImpl<>(Collections.singletonList(post));
		given(postRepositoryCustom.getMyBookmarkPosts(any(), anyBoolean(), anyString())).willReturn(expectedPage);

		//when
		Page<PostVo> resultPage = postRepositoryCustom.getMyBookmarkPosts(any(), anyBoolean(), anyString());

		//then
		assertThat(resultPage).isEqualTo(expectedPage);
		verify(postRepositoryCustom).getMyBookmarkPosts(any(), anyBoolean(), anyString());
	}

	@Test
	public void createPost_게시글_추가_성공() {
		//given
		PostDto.Request postDto = generatePostDto();
		generateUserAndPost();
		given(userRepository.findById(anyString())).willReturn(Optional.ofNullable(user));
		given(postRepository.save(any(Post.class))).willReturn(post);

		//when
		Long result = service.savePost(postDto, user.getId());

		//then
		assertThat(result).isEqualTo(post.getId());
	}

	@Test
	public void createPost_게시글_추가_실패() {
		//given
		PostDto.Request postDto = generatePostDto();
		generateUserAndPost();
		given(userRepository.findById(anyString())).willThrow(
			new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));

		//when
		assertThatThrownBy(() -> service.savePost(postDto, user.getId()))

			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception ->
				assertThat(((CustomAuthenticationException)exception).getErrorCode()).isEqualTo(
					ErrorCode.USER_NOT_FOUND)
			);
	}

	@Test
	public void updatePost_게시글_수정_성공() {
		//given
		PostDto.Request postDto = generatePostDto();
		generateUserAndPost();
		given(userRepository.findById(anyString())).willReturn(Optional.ofNullable(user));
		given(postRepository.findByIdAndIsRemoved(anyLong(), anyBoolean())).willReturn(Optional.ofNullable(post));

		//when
		Long result = service.updatePost(post.getId(), postDto, user.getId());

		//then
		assertThat(result).isEqualTo(post.getId());
	}

	@Test
	public void updatePost_게시글_수정_실패() {
		//given
		PostDto.Request postDto = generatePostDto();
		generateUserAndPost();
		given(userRepository.findById(anyString())).willReturn(Optional.ofNullable(user));
		given(postRepository.findByIdAndIsRemoved(anyLong(), anyBoolean())).willThrow(
			new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		//when
		assertThatThrownBy(() -> service.updatePost(post.getId(), postDto, user.getId()))
			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception ->
				assertThat(((CustomAuthenticationException)exception).getErrorCode()).isEqualTo(
					ErrorCode.POST_NOT_FOUND)
			);

	}

	@Test
	public void deletePost_게시글_삭제_성공() {
		//given
		generateUserAndPost();
		given(postRepository.findByIdAndIsRemoved(anyLong(), anyBoolean())).willReturn(Optional.ofNullable(post));

		//when
		Long result = service.removePost(user.getId(), post.getId());

		//then
		assertThat(result).isEqualTo(post.getId());
	}

	@Test
	public void deletePost_게시글_삭제_실패() {
		//given
		generateUserAndPost();
		given(postRepository.findByIdAndIsRemoved(anyLong(), anyBoolean())).willThrow(
			new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		//when
		assertThatThrownBy(() -> service.removePost(user.getId(), post.getId()))
			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception ->
				assertThat(((CustomAuthenticationException)exception).getErrorCode()).isEqualTo(
					ErrorCode.POST_NOT_FOUND)
			);
	}

}
