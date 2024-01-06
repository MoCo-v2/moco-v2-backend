package com.moco.moco.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.dto.BannerDto;
import com.moco.moco.service.post.BannerService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class AdminController {
	private final BannerService bannerService;

	@GetMapping("/admin/banners")
	public ResponseEntity<List<BannerDto.Response>> getBanners() {
		return ResponseEntity.ok().body(bannerService.getBanners());
	}

	@GetMapping("/admin/banners/{bannerId}")
	public ResponseEntity<BannerDto.Response> getBanner(@PathVariable Long bannerId) {
		return ResponseEntity.ok().body(bannerService.getBanner(bannerId));
	}

	@PostMapping("/admin/banners")
	public ResponseEntity<Void> saveBanner(@RequestBody BannerDto.Request request) {
		bannerService.saveBanner(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/admin/banners/{bannerId}")
	public ResponseEntity<Void> updateBanner(@PathVariable Long bannerId,
		@RequestBody BannerDto.Request request) {
		bannerService.updateBanner(bannerId, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
