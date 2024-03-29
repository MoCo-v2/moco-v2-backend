package com.moco.moco.service.post;

import static com.moco.moco.common.Validation.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moco.moco.domain.Comment;
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

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PostService {

	private UserRepository userRepository;
	private PostRepository postRepository;
	private PostRepositoryCustom postRepositoryCustom;
	private BookmarkRepositoryCustom bookmarkRepositoryCustom;
	private CommentRepository commentRepository;

	// 게시글을 페이징 한다.
	@Transactional(readOnly = true)
	public PostDto.Response getPosts(Integer offset,
		Integer limit,
		String recruit,
		String username,
		String type,
		String position,
		String mode,
		String language
	) {
		boolean isRecruit = "true".equalsIgnoreCase(recruit);
		PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "createdDate"));
		Page<PostVo> posts = postRepositoryCustom.getPosts(pageRequest, isRecruit, username, type, position, mode,
			language);

		List<PostDto.PostList> postLists = posts.getContent().stream()
			.map(PostDto.PostList::new)
			.collect(Collectors.toList());

		return new PostDto.Response(postLists, posts.getTotalPages(), posts.getTotalElements());
	}

	// 특정 게시글을 가져온다.
	@Transactional
	public PostVo getPost(Long postId) {
		validationPostId(postId);

		return postRepositoryCustom.getPost(postId)
			.orElseThrow(() -> new CustomAuthenticationException(
				ErrorCode.POST_NOT_FOUND));
	}

	// 마감이 얼마남지 않은 게시글을 가져온다
	@Transactional(readOnly = true)
	public List<PostDto.PostList> getPostsNearDeadline() {
		List<PostVo> postsNearDeadline = postRepositoryCustom.getPostsNearDeadline();
		return postsNearDeadline.stream()
			.map(PostDto.PostList::new)
			.collect(Collectors.toList());
	}

	// 게시글을 생성 한다.
	@Transactional
	public Long savePost(PostDto.Request postDto, String userId) {
		validationUserId(userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));
		postDto.setUser(user);

		return postRepository.save(postDto.toEntity()).getId();
	}

	// 게시글을 수정한다.
	@Transactional
	public Long updatePost(Long postId, PostDto.Request postDto, String userId) {
		validationUserIdAndPostId(userId, postId);

		userRepository.findById(userId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));

		Post post = postRepository.findByIdAndIsRemoved(postId, false)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		boolean isWriter = post.getUser().getId().equals(userId);
		if (!isWriter) {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}

		return post.update(postDto);
	}

	//게시글을 삭제한다.
	@Transactional
	public Long removePost(String userId, Long postId) {
		validationUserIdAndPostId(userId, postId);

		Post post = postRepository.findByIdAndIsRemoved(postId, false)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		boolean isWriter = post.getUser().getId().equals(userId);

		if (!isWriter) {
			throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED_WRITER);
		}

		Long deletedPostId = post.delete();
		commentRepository.findAllByPostId(deletedPostId).forEach(Comment::remove);
		
		bookmarkRepositoryCustom.deleteBookmarkedPost(deletedPostId);
		return deletedPostId;
	}

	//게시글 모집을 마감한다.
	@Transactional
	public void closeRecruitment(Long postId, String userId) {
		validationUserIdAndPostId(userId, postId);

		Post post = postRepository.findByIdAndIsRemoved(postId, false)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		boolean isWriter = post.getUser().getId().equals(userId);

		if (!isWriter) {
			throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED_WRITER);
		}

		post.close();
	}

	@Transactional(readOnly = true)
	public PostDto.Response getMyBookmarkPosts(Integer offset, Integer limit, String recruit, String userId) {
		boolean isRecruit = "true".equalsIgnoreCase(recruit);

		PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "postCreatedDate"));
		Page<PostVo> posts = postRepositoryCustom.getMyBookmarkPosts(pageRequest, isRecruit, userId);

		List<PostDto.PostList> postLists = posts.getContent().stream()
			.map(PostDto.PostList::new)
			.collect(Collectors.toList());

		return new PostDto.Response(postLists, posts.getTotalPages(), posts.getTotalElements());
	}
}