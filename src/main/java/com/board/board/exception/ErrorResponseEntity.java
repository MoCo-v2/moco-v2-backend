package com.board.board.exception;

import org.springframework.http.ResponseEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseEntity {
	private int status;
	private String name;
	private String code;
	private String message;

	public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e) {
		return ResponseEntity
			.status(e.getHttpStatus())
			.body(ErrorResponseEntity.builder()
				.status(e.getHttpStatus().value())
				.name(e.name())
				.message(e.getMessage())
				.build());
	}
}