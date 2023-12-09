package com.moco.moco.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommonResponseDto {
	private String msg;
	private Boolean result;

	@Builder
	public CommonResponseDto(String msg, Boolean result) {
		this.msg = msg;
		this.result = result;
	}
}
