package com.moco.moco.dto;

import com.moco.moco.domain.Banner;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class BannerDto {

	@Setter
	@Getter
	public static class Request {
		@NotNull(message = "배너의 순서를 작성해주세요")
		private Integer ordering;
		private String memo;
		private String title;
		private String content;
		private String description;
		private String backgroundColor;
		private String imageLink;
		private boolean expose;

		public Banner toEntity() {
			return Banner
				.builder()
				.ordering(ordering)
				.memo(memo)
				.title(title)
				.content(content)
				.description(description)
				.backgroundColor(backgroundColor)
				.imageLink(imageLink)
				.expose(expose)
				.build();
		}
	}

	@Setter
	@Getter
	public static class Response {
		private Long id;
		private int ordering;
		private String memo;
		private String title;
		private String content;
		private String description;
		private String backgroundColor;
		private String imageLink;
		private boolean expose;

		@Builder
		public Response(Banner banner) {
			this.id = banner.getId();
			this.ordering = banner.getOrdering();
			this.memo = banner.getMemo();
			this.title = banner.getTitle();
			this.content = banner.getContent();
			this.description = banner.getDescription();
			this.backgroundColor = banner.getBackgroundColor();
			this.imageLink = banner.getImageLink();
			this.expose = banner.isExpose();
		}
	}
}
