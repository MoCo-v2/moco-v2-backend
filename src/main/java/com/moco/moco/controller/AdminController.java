package com.moco.moco.controller;

import static com.moco.moco.common.ResponseEntityConstants.*;

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

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class AdminController {
	private final BannerService bannerService;

	@GetMapping("/admin/banners")
	public ResponseEntity<List<BannerDto.Response>> getBanners() {
		List<BannerDto.Response> bannersDto = bannerService.getBanners();
		return ResponseEntity.ok().body(bannersDto);
	}

	@GetMapping("/admin/banners/{bannerId}")
	public ResponseEntity<BannerDto.Response> getBanner(@PathVariable Long bannerId) {
		BannerDto.Response bannerDto = bannerService.getBanner(bannerId);
		return ResponseEntity.ok().body(bannerDto);
	}

	@PostMapping("/admin/banners")
	public ResponseEntity<BannerDto.Response> saveBanner(@Valid @RequestBody BannerDto.Request request) {
		BannerDto.Response bannerDto = bannerService.saveBanner(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(bannerDto);
	}

	@PutMapping("/admin/banners/{bannerId}")
	public ResponseEntity<BannerDto.Response> updateBanner(@PathVariable Long bannerId,
		@Valid @RequestBody BannerDto.Request request) {
		BannerDto.Response bannerDto = bannerService.updateBanner(bannerId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(bannerDto);
	}

	@DeleteMapping("/admin/banners/{bannerId}")
	public ResponseEntity<HttpStatus> deleteBanner(@PathVariable Long bannerId) {
		bannerService.deleteBanner(bannerId);
		return RESPONSE_ENTITY_NO_CONTENT;
	}
}
