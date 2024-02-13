package com.moco.moco.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.dto.BannerDto;
import com.moco.moco.service.admin.BannerService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class BannerController {
	private final BannerService bannerService;

	@GetMapping("/public/banners")
	public ResponseEntity<List<BannerDto.Response>> getBanner() {
		List<BannerDto.Response> bannerDto = bannerService.getExposeBanners();
		return ResponseEntity.ok().body(bannerDto);
	}
}
