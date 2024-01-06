package com.moco.moco.service.post;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moco.moco.domain.Banner;
import com.moco.moco.dto.BannerDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.repository.BannerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BannerService {

	private final BannerRepository bannerRepository;

	public List<BannerDto.Response> getBanners() {
		List<Banner> banners = bannerRepository.findAll();
		return banners.stream()
			.map(BannerDto.Response::new)
			.collect(Collectors.toList());
	}

	public BannerDto.Response getBanner(Long bannerId) {
		Banner banner = bannerRepository.findById(bannerId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.BANNER_NOT_FOUNT));
		return new BannerDto.Response(banner);
	}

	@Transactional
	public void saveBanner(BannerDto.Request request) {
		bannerRepository.save(request.toEntity());
	}

	@Transactional
	public void updateBanner(Long bannerId, BannerDto.Request request) {
		Banner banner = bannerRepository.findById(bannerId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.BANNER_NOT_FOUNT));

		banner.update(request);
	}
}
