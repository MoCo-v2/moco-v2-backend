package com.moco.moco.dto;

import java.util.List;

import com.moco.moco.domain.Banner;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class BannerDto {

	@Setter
	@Getter
	public static class Request {
		private String bannerName;
		private List<String> items;
		private boolean expose;

		public Banner toEntity() {
			return Banner
				.builder()
				.bannerName(bannerName)
				.imageList(items)
				.expose(expose)
				.build();
		}
	}

	@Setter
	@Getter
	public static class Response {
		private Long id;
		private String bannerName;
		private List<String> items;
		private boolean expose;

		@Builder
		public Response(Banner banner) {
			this.id = banner.getId();
			this.bannerName = banner.getBannerName();
			this.items = banner.getImageList();
			this.expose = banner.isExpose();
		}
	}
}
