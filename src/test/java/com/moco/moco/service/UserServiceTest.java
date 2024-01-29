package com.moco.moco.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moco.moco.domain.Role;
import com.moco.moco.domain.User;
import com.moco.moco.dto.UserDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.jpaRepository.UserRepository;
import com.moco.moco.service.user.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@InjectMocks
	UserService service;
	@Mock
	UserRepository userRepository;

	User user;
	UserDto.Request userDto;

	private void generateUser() {
		user = User.builder()
			.id("google1234")
			.name("test")
			.stack("[java,react]")
			.intro("1년차 백엔드 개발자 입니다.")
			.position("백엔드")
			.role(Role.USER)
			.picture("www.moco.run")
			.isDeleted(false)
			.build();
	}

	private void generateUserDto() {
		userDto = new UserDto.Request();
		userDto.setId("google1234");
		userDto.setName("test");
		userDto.setCareer("1년차");
		userDto.setIntro("1년차 백엔드 개발자입니다.");
		userDto.setPosition("백엔드");
		userDto.setStack("[java,react]");
	}

	@Test
	public void getUser_유저_조회_성공() {
		//given
		generateUser();
		given(userRepository.findById(anyString())).willReturn(Optional.ofNullable(user));

		//when
		UserDto.Response result = service.getUser(user.getId());

		//then
		assertThat(result.getId()).isEqualTo(user.getId());
	}

	@Test
	public void getUser_유저_조회_실패() {
		//given
		generateUser();
		given(userRepository.findById(anyString())).willThrow(
			new CustomAuthenticationException(ErrorCode.USER_NOT_FOUND));

		//when
		assertThatThrownBy(() -> service.getUser(user.getId()))
			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception ->
				assertThat(((CustomAuthenticationException)exception).getErrorCode()).isEqualTo(
					ErrorCode.USER_NOT_FOUND));
	}

	@Test
	public void createUser_유저_추가_실패_ID_중복_케이스() {
		//given
		generateUserDto();
		generateUser();
		
		given(userRepository.findById(anyString())).willReturn(Optional.ofNullable(user));

		//when
		assertThatThrownBy(() -> service.join(userDto))
			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception ->
				assertThat(((CustomAuthenticationException)exception).getErrorCode()).isEqualTo(
					ErrorCode.DUPLICATE_RESOURCE)
			);
	}

}
