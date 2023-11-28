package com.board.board.service.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.board.board.domain.User;
import com.board.board.dto.UserDto;
import com.board.board.repository.PostRepository;
import com.board.board.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PostRepository boardRepository;

	/* 회원가입 */
	@Transactional
	public User join(UserDto.Request userDto) {
		return userRepository.save(userDto.toEntity());
	}

	/* 회원가입 시 input 유효성 체크 */
	@Transactional(readOnly = true)
	public Map<String, String> validateHandling(Errors errors) {
		Map<String, String> validatorResult = new HashMap<>();

		/* 유효성 검사에 실패한 필드 목록을 받음 */
		for (FieldError error : errors.getFieldErrors()) {
			String validKeyName = String.format("valid_%s", error.getField());
			validatorResult.put(validKeyName, error.getDefaultMessage());
		}
		return validatorResult;
	}

	/* 회원가입시 이메일 중복 여부 */
	@Transactional(readOnly = true)
	public boolean checkUseremailDuplication(String email) {
		boolean useremailDuplication = userRepository.existsByEmail(email);

		//중복 = true
		return useremailDuplication;
	}

	/* 회원가입시 별명 중복 여부 */
	@Transactional(readOnly = true)
	public boolean checkUsernameDuplication(String name) {
		boolean usernameDuplication = userRepository.existsByName(name);

		return usernameDuplication;
	}

	/* 설정에서 별명 바꾸기 */
	@Transactional
	public User nameUpdateInSetting(Long userid, String name) {
		User user = userRepository.findById(userid).orElseThrow(() ->
			new IllegalArgumentException("유저를 찾을수 없습니다."));
		user.updateNameInSetting(name);
		/* 해당 유저가 작성한 게시글 작성자도 변경 */
		boardRepository.updateWriter(name, user.getId());
		return user;
	}

	/* 설정에서 프로필 사진 변경 */
	@Transactional
	public User profileUpdateInSetting(Long userId, String imgURL) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		user.updateProfile(imgURL);
		return user;
	}

	/* 회원탈퇴 */
	@Transactional
	public void deleteUser(Long userId) {
		userRepository.deleteById(userId);
	}
}
