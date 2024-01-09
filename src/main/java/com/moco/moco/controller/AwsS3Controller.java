package com.moco.moco.controller;

import static com.moco.moco.common.ResponseEntityConstants.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.moco.moco.service.aws.AwsS3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AwsS3Controller {
	private final AwsS3Service awsS3Service;

	// @return 성공 시 200 Success와 함께 업로드 된 파일의 파일명 반환
	@PostMapping("/private/images")
	public ResponseEntity<String> uploadImage(
		@RequestParam(value = "image") MultipartFile multipartFile) {
		String imageUrl = awsS3Service.uploadImage(multipartFile);
		return ResponseEntity.status(HttpStatus.OK).body(imageUrl);
	}

	@DeleteMapping("/private/images")
	public ResponseEntity<HttpStatus> deleteImage(@RequestBody String fileName) {
		awsS3Service.deleteImage(fileName);
		return RESPONSE_ENTITY_NO_CONTENT;
	}
}
