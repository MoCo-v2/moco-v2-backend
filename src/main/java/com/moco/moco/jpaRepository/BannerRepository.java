package com.moco.moco.jpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moco.moco.domain.Banner;

public interface BannerRepository extends JpaRepository<Banner, Long> {
	List<Banner> findByExposeTrueOrderByOrderingAsc();
}
