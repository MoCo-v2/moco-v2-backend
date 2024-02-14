package com.moco.moco.domain;

import com.moco.moco.dto.BannerDto;

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

	@Column(name = "ordering", nullable = false)
	private Integer ordering;

	@Column(name = "memo")
	private String memo;

	@Column(name = "title")
	private String title;

	@Column(name = "content")
	private String content;

	@Column(name = "description")
	private String description;

	@Column(name = "backgroundColor")
	private String backgroundColor;

	@Column(name = "imageLink")
	private String imageLink;

	@Column(name = "pageLink")
	private String pageLink;

	@Column(name = "expose")
	private boolean expose = false;

	@Builder
	public Banner(int ordering, String memo, String title, String content, String description, String backgroundColor,
		String imageLink, String pageLink, boolean expose) {
		this.ordering = ordering;
		this.memo = memo;
		this.title = title;
		this.content = content;
		this.description = description;
		this.backgroundColor = backgroundColor;
		this.imageLink = imageLink;
		this.pageLink = pageLink;
		this.expose = expose;
	}

	public Banner update(BannerDto.Request request) {
		this.ordering = request.getOrdering();
		this.memo = request.getMemo();
		this.title = request.getTitle();
		this.content = request.getContent();
		this.description = request.getDescription();
		this.backgroundColor = request.getBackgroundColor();
		this.imageLink = request.getImageLink();
		this.pageLink = request.getPageLink();
		this.expose = request.isExpose();
		return this;
	}
}
