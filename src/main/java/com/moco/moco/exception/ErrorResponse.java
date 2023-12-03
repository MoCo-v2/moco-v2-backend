package com.moco.moco.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
	private int status;
	private String message;

	public static ResponseEntity<ErrorResponse> toResponseEntity(HttpStatus status, String
		msg) {
		return ResponseEntity
			.status(status)
			.body(ErrorResponse.builder()
				.status(status.value())
				.message(msg)
				.build());
	}
}