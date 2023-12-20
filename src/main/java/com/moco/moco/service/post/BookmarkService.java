package com.moco.moco.service.post;

import org.springframework.stereotype.Service;

import com.moco.moco.domain.Bookmark;
import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;
import com.moco.moco.dto.BookmarkDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.repository.BookmarkRepository;
import com.moco.moco.repository.PostRepository;
import com.moco.moco.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class BookmarkService {

	private final BookmarkRepository bookMarkRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	public BookmarkDto.Response createBookmark(String userId, Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.POST_NOT_FOUND));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));

		Bookmark bookmark = Bookmark.builder().post(post).user(user).build();
		return new BookmarkDto.Response(bookMarkRepository.save(bookmark));
	}

	public BookmarkDto.Response removeBookmark(String userId, Long postId) {
		Bookmark bookmark = bookMarkRepository.findByUserIdAndPostId(userId, postId);

		if (!bookmark.getUser().getId().equals(userId)) {
			throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED_WRITER);
		}

		bookMarkRepository.delete(bookmark);

		return new BookmarkDto.Response(bookMarkRepository.save(bookmark));
	}
}
