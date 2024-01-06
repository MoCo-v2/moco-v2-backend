package com.moco.moco.service.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
	private final CommentRepository commentRepository;
	private final CommentRepositoryCustom commentRepositoryCustom;
	private final UserRepository userRepository;
	private final PostRepository postRepository;

	public List<CommentDto.Response> getComments(Long postId) {
		postRepository.findById(postId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		List<Comment> comments = commentRepositoryCustom.getComments(postId);

		return convertNestedStructure(comments);
	}

	@Transactional
	public CommentDto.Response createComment(String userId, Long postId, CommentDto.Request commentDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));

		Comment parentComment = null;
		if (commentDto.getParentId() != null) {
			parentComment = commentRepository.findById(commentDto.getParentId())
				.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.COMMENT_NOT_FOUND));
			if (parentComment.getParent() != null) {
				throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
			}
		}

		commentDto.setParentComment(parentComment);
		commentDto.setUser(user);
		commentDto.setPost(post);

		Comment savedComment = commentRepository.save(commentDto.toEntity());

		commentRepositoryCustom.addCommentCount(postId, 1);

		return new CommentDto.Response(savedComment);
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

		if (comment.isRemoved()) {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}

		if (!comment.getUser().getId().equals(userId)) {
			throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED_WRITER);
		}

		if (!comment.getChildList().isEmpty()) {
			comment.remove();
		} else {
			commentRepository.delete(getDeletableAncestorComment(comment));
		}

		commentRepositoryCustom.addCommentCount(comment.getPost().getId(), -1);
	}

	private Comment getDeletableAncestorComment(Comment comment) {
		Comment parent = comment.getParent(); // 현재 댓글의 부모를 구함
		if (parent != null && parent.getChildList().size() == 1 && parent.isRemoved())
			// 부모가 있고, 부모의 자식이 1개(지금 삭제하는 댓글)이고, 부모의 삭제 상태가 TRUE인 댓글이라면 재귀
			return getDeletableAncestorComment(parent);
		return comment; // 삭제해야하는 댓글 반환
	}

	public List<CommentDto.Response> convertNestedStructure(List<Comment> comments) {
		List<CommentDto.Response> result = new ArrayList<>();
		Map<Long, CommentDto.Response> map = new HashMap<>();

		comments.forEach(comment -> {
			CommentDto.Response commentDto = new CommentDto.Response(comment);
			if (comment.isRemoved()) {
				commentDto.setContent("삭제된 댓글입니다.");
			}

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
