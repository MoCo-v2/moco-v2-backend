package com.moco.moco.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moco.moco.dto.BookmarkDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.jpaRepository.BookmarkRepositoryCustom;
import com.moco.moco.service.post.BookmarkService;

@ExtendWith(MockitoExtension.class)
public class BookmarkServiceTest {

	@Spy
	@InjectMocks
	BookmarkService service;

	@Mock
	BookmarkRepositoryCustom bookmarkRepositoryCustom;

	@Test
	public void getMyBookmarks_내_북마크_가져오기_성공() {

		List<Long> ids = new ArrayList<>();
		ids.add(1L);
		ids.add(2L);

		//given
		given(bookmarkRepositoryCustom.getMyBookmarks(anyString())).willReturn(ids);
		BookmarkDto.MyBookmark myBookmark = new BookmarkDto.MyBookmark(ids);
		given(service.getMyBookmarks("google1234567")).willReturn(myBookmark);

		// when
		BookmarkDto.MyBookmark result = service.getMyBookmarks("google1234567");

		// then
		assertThat(result.getIdList()).isEqualTo(ids);
	}

	@Test
	public void getMyBookmarks_내_북마크_가져오기_실패() {
		
		//when
		assertThatThrownBy(() -> service.getMyBookmarks("emptyUser"))

			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception ->
				assertThat(((CustomAuthenticationException)exception).getErrorCode()).isEqualTo(
					ErrorCode.BAD_REQUEST)
			);
	}

}
