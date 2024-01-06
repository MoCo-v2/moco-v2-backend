package com.moco.moco.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.dto.BannerDto;
import com.moco.moco.service.admin.BannerService;

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
	public ResponseEntity<BannerDto.Response> saveBanner(@RequestBody BannerDto.Request request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(bannerService.saveBanner(request));
	}

	@PutMapping("/admin/banners/{bannerId}")
	public ResponseEntity<BannerDto.Response> updateBanner(@PathVariable Long bannerId,
		@RequestBody BannerDto.Request request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(bannerService.updateBanner(bannerId, request));
	}

	@DeleteMapping("/admin/banners/{bannerId}")
	public ResponseEntity<Void> deleteBanner(@PathVariable Long bannerId) {
		bannerService.deleteBanner(bannerId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
