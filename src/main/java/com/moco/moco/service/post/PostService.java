package com.moco.moco.service.post;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moco.moco.domain.Comment;
import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;
import com.moco.moco.dto.PostDto;
import com.moco.moco.dto.queryDslDto.PostDetailVo;
import com.moco.moco.dto.queryDslDto.PostVo;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.repository.PostRepository;
import com.moco.moco.repository.PostRepositoryCustom;
import com.moco.moco.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PostService {

	private UserRepository userRepository;
	private PostRepository postRepository;
	private PostRepositoryCustom postRepositoryCustom;

	private CommentService commentService;

	// 게시글을 페이징 한다.
	@Transactional(readOnly = true)
	public PostDto.Response getPosts(Integer offset, Integer limit, String recruit, String username) {
		boolean isRecruit;
		if ("true".equalsIgnoreCase(recruit)) {
			isRecruit = true;
		} else if ("false".equalsIgnoreCase(recruit)) {
			isRecruit = false;
		} else {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}

		PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "created_date"));
		List<PostVo> posts = postRepositoryCustom.getPosts(pageRequest, isRecruit, username);
		Long total = getPostCount();

		return new PostDto.Response(posts, total);
	}

	// 특정 게시글을 가져온다.
	@Transactional
	public PostDetailVo getPost(Long postId) {
		PostDetailVo post = postRepositoryCustom.getPost(postId)
			.orElseThrow(() -> new CustomAuthenticationException(
				ErrorCode.POST_NOT_FOUND));

		//댓글 계층 구조로 정렬한다.
		List<Comment> comments = postRepositoryCustom.getComments(post.getId());
		post.setComments(commentService.convertNestedStructure(comments));

		return post;
	}

	// 게시글을 생성 한다.
	@Transactional
	public Long savePost(PostDto.Request postDto, String userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));
		postDto.setUser(user);

		return postRepository.save(postDto.toEntity()).getId();
	}

	// 게시글을 수정한다.
	@Transactional
	public Long updatePost(Long postId, PostDto.Request postDto, String userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		Boolean isWriter = post.getUser().getId().equals(userId);
		if (!isWriter) {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}

		return post.update(postDto);
	}

	@Transactional
	public Long removePost(String userId, Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		Boolean isWriter = post.getUser().getId().equals(userId);

		if (!isWriter) {
			throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED_WRITER);
		}

		return post.delete();
	}

	// /* 게시글 검색 */
	// @Transactional
	// public PostDto.Posts searchPosts(Integer offset, Integer limit, String keyword) {
	// 	PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "created_date"));
	// 	List<PostListVo> posts = postRepositoryCustom.getSearchPost(pageRequest, keyword);
	// 	Long total = getPostCount();
	// 	return new PostDto.Posts(posts, total);
	// }

	/* 페이징 */
	@Transactional
	public Long getPostCount() {
		return postRepository.count();
	}

}