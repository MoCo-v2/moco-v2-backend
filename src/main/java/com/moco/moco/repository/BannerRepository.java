package com.moco.moco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moco.moco.domain.Banner;

public interface BannerRepository extends JpaRepository<Banner, Long> {
	
}
