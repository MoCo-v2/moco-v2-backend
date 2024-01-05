package com.moco.moco.service.post;

import java.util.List;
import java.util.Map;

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

	public List<Banner> getBanner() {
		return bannerRepository.findAll();
	}

	@Transactional
	public void saveBanner(BannerDto.Request request) {
		Map<String, Object> items = Map.of("bannerImages", request.getImages());

		Banner banner = Banner.of(items);
		bannerRepository.save(banner);
	}

	public void updateBanner(Long bannerId, List<String> images) {
		Banner banner = bannerRepository.findById(bannerId)
			.orElseThrow(() -> new CustomAuthenticationException(ErrorCode.BANNER_NOT_FOUNT));

		Map<String, Object> items = Map.of("bannerImages", images);

		banner.update(items);
	}
}
