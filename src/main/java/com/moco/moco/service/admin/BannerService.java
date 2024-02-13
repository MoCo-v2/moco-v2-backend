package com.moco.moco.service.admin;

import static com.moco.moco.common.Validation.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moco.moco.domain.Banner;
import com.moco.moco.dto.BannerDto;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;
import com.moco.moco.jpaRepository.BannerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BannerService {

	private final BannerRepository bannerRepository;

	@Transactional(readOnly = true)
	public List<BannerDto.Response> getBanners() {
		List<Banner> banners = bannerRepository.findAll();
		return banners.stream()
			.map(BannerDto.Response::new)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public BannerDto.Response getBanner(Long bannerId) {
		validationBannerId(bannerId);

		Banner banner = bannerRepository.findById(bannerId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.BANNER_NOT_FOUNT));
		return new BannerDto.Response(banner);
	}

	@Transactional
	public BannerDto.Response saveBanner(BannerDto.Request request) {
		Banner banner = bannerRepository.save(request.toEntity());
		return new BannerDto.Response(banner);
	}

	@Transactional
	public BannerDto.Response updateBanner(Long bannerId, BannerDto.Request request) {
		validationBannerId(bannerId);

		Banner banner = bannerRepository.findById(bannerId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.BANNER_NOT_FOUNT));

		return new BannerDto.Response(banner.update(request));
	}

	@Transactional
	public void deleteBanner(Long bannerId) {
		validationBannerId(bannerId);

		Banner banner = bannerRepository.findById(bannerId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.BANNER_NOT_FOUNT));

		bannerRepository.delete(banner);
	}

	@Transactional(readOnly = true)
	public List<BannerDto.Response> getExposeBanners() {
		List<Banner> banners = bannerRepository.findByExposeTrueOrderByOrderingAsc();
		return banners.stream()
			.map(BannerDto.Response::new)
			.collect(Collectors.toList());
	}
}
