package com.moco.moco.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moco.moco.domain.Comment;
import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;
import com.moco.moco.dto.CommentDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.repository.CommentRepository;
import com.moco.moco.repository.CommentRepositoryCustom;
import com.moco.moco.repository.PostRepository;
import com.moco.moco.repository.UserRepository;
import com.moco.moco.service.post.CommentService;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@InjectMocks
	CommentService service;

	@Mock
	CommentRepositoryCustom commentRepositoryCustom;
	@Mock
	CommentRepository commentRepository;
	@Mock
	PostRepository postRepository;
	@Mock
	UserRepository userRepository;

	@Test
	void getComments_댓글_조회_성공() {
		//given
		Post post = Post.builder().id(1L).build();
		List<Comment> comments = new ArrayList<>();
		given(postRepository.findById(1L)).willReturn(Optional.ofNullable(post));
		given(commentRepositoryCustom.getComments(1L)).willReturn(comments);

		//when
		List result = service.getComments(1L);

		//then
		assertThat(result).isEqualTo(comments);
	}

	@Test
	void getComments_댓글_조회_실패() {
		//given
		given(postRepository.findById(1L)).willThrow(new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		//when
		assertThatThrownBy(() -> service.getComments(1L))
			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception -> assertThat(((CustomAuthenticationException)exception).getErrorCode())
				.isEqualTo(ErrorCode.POST_NOT_FOUND));
	}

	@Test
	void createComment_댓글_생성_성공() {
		//given
		User user = User.builder().id("google1234").build();
		Post post = Post.builder().id(1L).build();
		CommentDto.Request commentDto = new CommentDto.Request();
		Comment comment = Comment.builder().id(1L).user(user).post(post).build();
		given(userRepository.findById("google1234")).willReturn(Optional.ofNullable(user));
		given(postRepository.findById(1L)).willReturn(Optional.ofNullable(post));
		given(commentRepository.save(any())).willReturn(comment);

		//when
		CommentDto.Response result = service.createComment(user.getId(), post.getId(), commentDto);

		//then
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getPostId()).isEqualTo(post.getId());
	}

	@Test
	void createComment_댓글_생성_실패_존재하지_않는_유저_ID() {
		//given
		User user = User.builder().id("google1234").build();
		Post post = Post.builder().id(1L).build();
		CommentDto.Request commentDto = new CommentDto.Request();
		given(userRepository.findById("google1234")).willThrow(
			new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));

		//when
		assertThatThrownBy(() -> service.createComment(user.getId(), post.getId(), commentDto))
			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception ->
				assertThat(((CustomAuthenticationException)exception).getErrorCode()).isEqualTo(
					ErrorCode.USER_NOT_FOUND)
			);
	}

	@Test
	void createComment_댓글_생성_실패_존재하지_않는_게시글_ID() {
		//given
		User user = User.builder().id("google1234").build();
		Post post = Post.builder().id(1L).build();
		CommentDto.Request commentDto = new CommentDto.Request();
		given(userRepository.findById(anyString())).willReturn(Optional.ofNullable(user));
		given(postRepository.findById(anyLong())).willThrow(
			new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		//when
		assertThatThrownBy(() -> service.createComment(user.getId(), post.getId(), commentDto))
			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception ->
				assertThat(((CustomAuthenticationException)exception).getErrorCode()).isEqualTo(
					ErrorCode.POST_NOT_FOUND)
			);
	}

	@Test
	void updateComment_댓글_수정_성공() {
		//given
		User user = User.builder().id("google1234").build();
		Post post = Post.builder().id(1L).build();
		Comment comment = Comment.builder().id(1L).user(user).post(post).build();
		CommentDto.Request dto = new CommentDto.Request();
		given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(comment));

		//when
		CommentDto.Response result = service.updateComment("google1234", 1L, dto);

		//then
		assertThat(result.getId()).isEqualTo(comment.getId());
	}

	@Test
	void updateComment_댓글_수정_실패() {
		//given
		CommentDto.Request dto = new CommentDto.Request();
		given(commentRepository.findById(anyLong())).willThrow(
			new CustomAuthenticationException(ErrorCode.COMMENT_NOT_FOUND));

		//when
		assertThatThrownBy(() -> service.updateComment("google1234", 1L, dto))
			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception ->
				assertThat(((CustomAuthenticationException)exception).getErrorCode()).isEqualTo(
					ErrorCode.COMMENT_NOT_FOUND)
			);
	}

	@Test
	void deleteComment_댓글_삭제_성공() {
		//given
		User user = User.builder().id("google1234").build();
		Post post = Post.builder().id(1L).build();
		Comment comment = Comment.builder().id(1L).user(user).post(post).isRemoved(false).build();
		given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(comment));

		//when
		service.deleteComment(user.getId(), comment.getId());

		//then
		assertThat(comment.isRemoved()).isEqualTo(true);
	}

	@Test
	void deleteComment_댓글_삭제_실패() {
		//given
		User user = User.builder().id("google1234").build();
		Post post = Post.builder().id(1L).build();
		Comment comment = Comment.builder().id(1L).user(user).post(post).isRemoved(false).build();
		given(commentRepository.findById(anyLong())).willThrow(
			new CustomAuthenticationException(ErrorCode.COMMENT_NOT_FOUND));

		//when
		assertThatThrownBy(() -> service.deleteComment(user.getId(), comment.getId()))
			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception ->
				assertThat(((CustomAuthenticationException)exception).getErrorCode()).isEqualTo(
					ErrorCode.COMMENT_NOT_FOUND)
			);
	}
}