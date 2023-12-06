package com.moco.moco.service.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;
import com.moco.moco.dto.RecruitDto;
import com.moco.moco.repository.PostRepository;
import com.moco.moco.repository.RecruitRepositoey;
import com.moco.moco.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RecruitService {
	private RecruitRepositoey recruitRepositoey;
	private PostRepository postRepository;
	private UserRepository userRepository;

	/* 모집참가 */
	@Transactional
	public Long join(Long boardid, String userId, RecruitDto.Request recruitDto) {
		Post post = postRepository.findById(boardid)
			.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을수 없습니다."));
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저를 찾을수 없습니다."));
		;

		recruitDto.setPost(post);
		recruitDto.setUser(user);
		recruitRepositoey.save(recruitDto.toEntity());
		return recruitDto.getId();
	}

	/* 참가여부 조회 */
	@Transactional(readOnly = true)
	public Boolean isDuplicate(Long boardId, Long userId) {
		return recruitRepositoey.existsByBoardIdAndUserId(boardId, userId);
	}

	/* 해당게시글 참가 인원수 가져오기 */
	@Transactional(readOnly = true)
	public Long countToJoinUsers(Long boardId) {
		return recruitRepositoey.countByBoardId(boardId);
	}

	/* 참가 취소 */
	@Transactional
	public int joinCancel(Long boardId, Long userId) {
		return recruitRepositoey.joinUserCancel(boardId, userId);
	}

}
