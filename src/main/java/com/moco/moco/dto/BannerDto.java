package com.moco.moco.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class BannerDto {

	@Getter
	@Setter
	public static class Request {
		private List<String> images;
	}
}
