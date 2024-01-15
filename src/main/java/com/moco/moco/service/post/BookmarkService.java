package com.moco.moco.service.post;

import static com.moco.moco.common.Validation.*;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.moco.moco.domain.Bookmark;
import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;
import com.moco.moco.dto.BookmarkDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.jpaRepository.BookmarkRepository;
import com.moco.moco.jpaRepository.BookmarkRepositoryCustom;
import com.moco.moco.jpaRepository.PostRepository;
import com.moco.moco.jpaRepository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class BookmarkService {

	private final BookmarkRepositoryCustom bookmarkRepositoryCustom;
	private final BookmarkRepository bookMarkRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	public BookmarkDto.Response createBookmark(String userId, Long postId) {
		validationUserId(userId);
		validationPostId(postId);

		boolean isExistsBookmark = bookmarkRepositoryCustom.isBookmarkExists(userId, postId);
		if (isExistsBookmark) {
			throw new CustomAuthenticationException(ErrorCode.DUPLICATE_RESOURCE);
		}

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));

		Bookmark bookmark = Bookmark.builder().post(post).user(user).build();

		Bookmark saveBookmark;
		try {
			saveBookmark = bookMarkRepository.save(bookmark);
		} catch (DataIntegrityViolationException e) {
			// 유니크 제약 위배 예외 처리
			throw new CustomAuthenticationException(ErrorCode.DUPLICATE_RESOURCE);
		}
		return new BookmarkDto.Response(saveBookmark);
	}

	public void removeBookmark(String userId, Long postId) {
		validationUserId(userId);
		validationPostId(postId);

		Optional<Bookmark> bookmark = Optional.ofNullable(bookMarkRepository.findByUserIdAndPostId(userId, postId));

		if (bookmark.isEmpty()) {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}

		if (!bookmark.get().getUser().getId().equals(userId)) {
			throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED_WRITER);
		}

		bookMarkRepository.delete(bookmark.get());
	}
}
