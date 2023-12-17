package com.moco.moco.service.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.moco.moco.domain.Comment;
import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;
import com.moco.moco.dto.CommentDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.repository.CommentRepository;
import com.moco.moco.repository.PostRepository;
import com.moco.moco.repository.PostRepositoryCustom;
import com.moco.moco.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final PostRepositoryCustom postRepositoryCustom;

	private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Transactional
	public CommentDto.Response createComment(String userId, Long postId, CommentDto.Request commentDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		if (commentDto.getParentId() != null) {
			Comment parentComment = commentRepository.findById(commentDto.getParentId())
				.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.COMMENT_NOT_FOUND));
			commentDto.setParentComment(parentComment);
		} else {
			commentDto.setParentComment(null);
		}

		commentDto.setUser(user);
		commentDto.setPost(post);
		postRepositoryCustom.addCommentCount(postId, +1);
		Comment comment = commentRepository.save(commentDto.toEntity());
		return new CommentDto.Response(comment);
	}

	@Transactional
	public CommentDto.Response updateComment(String userId, Long commentId, CommentDto.Request commentDto) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
			new CustomAuthenticationException(ErrorCode.COMMENT_NOT_FOUND));

		if (!comment.getUser().getId().equals(userId)) {
			throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED_WRITER);
		}

		return new CommentDto.Response(comment.update(commentDto.getContent()));
	}

	@Transactional
	public void deleteComment(String userId, Long commentId) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
			new CustomAuthenticationException(ErrorCode.COMMENT_NOT_FOUND));

		if (!comment.getUser().getId().equals(userId)) {
			throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED_WRITER);
		}

		comment.remove();
		postRepositoryCustom.addCommentCount(comment.getPost().getId(), -1);
	}

	public List<CommentDto.Response> convertNestedStructure(List<Comment> comments) {
		List<CommentDto.Response> result = new ArrayList<>();
		Map<Long, CommentDto.Response> map = new HashMap<>();

		comments.stream().forEach(comment -> {
			CommentDto.Response commentDto = new CommentDto.Response(comment);
			if (comment.getParent() != null) {
				commentDto.setParentId(comment.getParent().getId());
			}
			map.put(commentDto.getId(), commentDto);
			if (comment.getParent() != null) {
				map.get(comment.getParent().getId()).getChildList().add(commentDto);
			} else {
				result.add(commentDto);
			}
		});

		return result;
	}
}
