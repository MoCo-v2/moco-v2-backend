package com.board.board.service.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.board.domain.Like;
import com.board.board.domain.Post;
import com.board.board.domain.User;
import com.board.board.dto.LikeDto;
import com.board.board.repository.LikeRepository;
import com.board.board.repository.PostRepository;
import com.board.board.repository.PostRepositoryCustom;
import com.board.board.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LikeService {
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final LikeRepository likeRepository;
	private final PostRepositoryCustom boardRepositoryCustom;

	/* CREATE */
	@Transactional
	public Long likeSave(String name, Long boardId) {
		User user = userRepository.findByName(name);
		Post board = postRepository.findById(boardId).orElseThrow(() ->
			new IllegalArgumentException("좋아요 실패 : 해당 게시글이 존재하지 않습니다." + boardId));

		LikeDto.Request likeDto = new LikeDto.Request();
		likeDto.setUser(user);
		likeDto.setPost(board);
		likeRepository.save(likeDto.toEntity());
		boardRepositoryCustom.updateLikeCountPlus(boardId);

		return likeDto.getId();
	}

	/* READ */
	@Transactional(readOnly = true)
	public boolean findLike(Long userId, Long boardId) {
		return likeRepository.existsByUser_IdAndPost_Id(userId, boardId);
	}

	/* READ COUNT */
	@Transactional(readOnly = true)
	public Long findLikeCount(Long boardId) {
		return likeRepository.countByPost_Id(boardId);
	}

	/* DELETE */
	@Transactional
	public Long deleteLike(String name, Long boardId) {
		User user = userRepository.findByName(name);
		Like like = likeRepository.findByUser_IdAndPost_Id(user.getId(), boardId);
		likeRepository.delete(like);
		boardRepositoryCustom.updateLikeCountMinus(boardId);
		return like.getId();
	}

}
