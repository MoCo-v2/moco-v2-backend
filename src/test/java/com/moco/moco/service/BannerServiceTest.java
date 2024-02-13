package com.moco.moco.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moco.moco.domain.Banner;
import com.moco.moco.dto.BannerDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.jpaRepository.BannerRepository;
import com.moco.moco.service.admin.BannerService;

@ExtendWith(MockitoExtension.class)
public class BannerServiceTest {

	@Spy
	@InjectMocks
	BannerService service;

	@Mock
	BannerRepository bannerRepository;

	@Test
	void getBanner_배너_조회_성공() {
		Banner banner = Banner.builder().imageLink("moco.run/image/123").expose(false).build();
		BannerDto.Response bannerDto = new BannerDto.Response(banner);
		//given
		given(bannerRepository.findById(anyLong())).willReturn(Optional.of(banner));
		given(service.getBanner(1L)).willReturn(bannerDto);

		//when
		BannerDto.Response result = service.getBanner(1L);

		//then
		assertThat(result).isEqualTo(bannerDto);
	}

	@Test
	void getBanner_배너_조회_실패() {
		//given
		given(bannerRepository.findById(anyLong())).willThrow(
			new CustomAuthenticationException(ErrorCode.BANNER_NOT_FOUNT));

		//when
		assertThatThrownBy(() -> service.getBanner(1L))
			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception -> assertThat(((CustomAuthenticationException)exception).getErrorCode())
				.isEqualTo(ErrorCode.BANNER_NOT_FOUNT));
	}

	@Test
	void getBanners_배너_목록_조회_성공() {
		Banner banner = Banner.builder().imageLink("moco.run/image/123").expose(false).build();
		BannerDto.Response bannerDto = new BannerDto.Response(banner);
		List<BannerDto.Response> bannerDtos = new ArrayList<>();
		bannerDtos.add(bannerDto);

		//given
		given(service.getBanners()).willReturn(bannerDtos);

		//when
		List<BannerDto.Response> result = service.getBanners();

		//then
		assertThat(result).isEqualTo(bannerDtos);
	}

	@Test
	void createBanner_배너_생성_성공() {
		Banner banner = Banner.builder().ordering(100).imageLink("moco.run/image/123").expose(false).build();
		BannerDto.Request request = new BannerDto.Request();
		request.setOrdering(100);

		//given
		given(bannerRepository.save(any())).willReturn(banner);

		//when
		BannerDto.Response result = service.saveBanner(request);

		//then
		assertThat(result.getImageLink()).isEqualTo(banner.getImageLink());
	}

	@Test
	void updateBanner_배너_수정_성공() {
		Banner banner = Banner.builder().ordering(100).imageLink("moco.run/image/123").expose(false).build();
		BannerDto.Request request = new BannerDto.Request();
		request.setOrdering(100);
		request.setImageLink("moco.run/image/111");
		request.setExpose(false);

		//given
		given(bannerRepository.findById(any())).willReturn(Optional.ofNullable(banner));

		//when
		BannerDto.Response result = service.updateBanner(1L, request);

		//then
		assertThat(result.getImageLink()).isEqualTo(request.getImageLink());
	}

	@Test
	void updateBanner_배너_수정_실패() {
		BannerDto.Request request = new BannerDto.Request();
		request.setImageLink("moco.run/image/123");
		request.setExpose(false);

		//given
		given(bannerRepository.findById(anyLong())).willThrow(
			new CustomAuthenticationException(ErrorCode.BANNER_NOT_FOUNT));

		//when
		assertThatThrownBy(() -> service.updateBanner(1L, request))
			//then
			.isInstanceOf(CustomAuthenticationException.class)
			.satisfies(exception -> assertThat(((CustomAuthenticationException)exception).getErrorCode())
				.isEqualTo(ErrorCode.BANNER_NOT_FOUNT));
	}
}
