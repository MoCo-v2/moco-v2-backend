package com.moco.moco.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moco.moco.domain.Banner;
import com.moco.moco.dto.BannerDto;
import com.moco.moco.service.post.BannerService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class MasterController {
	private final BannerService bannerService;

	@GetMapping("/public/banners")
	public ResponseEntity<List<Banner>> getBanners() {
		return ResponseEntity.ok().body(bannerService.getBanner());
	}

	@PostMapping("/public/banners")
	public ResponseEntity<Void> saveBanner(@RequestBody BannerDto.Request request) {
		bannerService.saveBanner(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
