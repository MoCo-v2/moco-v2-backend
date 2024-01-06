package com.moco.moco.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Type;

import com.moco.moco.dto.BannerDto;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "banner")
public class Banner extends Time {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String bannerName;

	@Type(ListArrayType.class)
	@Column(columnDefinition = "text[]")
	private List<String> imageList = new ArrayList<>();

	@Column
	private boolean expose = false;

	@Builder
	public Banner(String bannerName, List<String> imageList, boolean expose) {
		this.bannerName = bannerName;
		this.imageList = imageList;
		this.expose = expose;
	}

	public Banner update(BannerDto.Request request) {
		this.bannerName = request.getBannerName();
		this.imageList = request.getItems();
		this.expose = request.isExpose();
		return this;
	}
}
