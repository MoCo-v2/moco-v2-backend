package com.moco.moco.controller;

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

	/** @return 성공 시 200 Success와 함께 업로드 된 파일의 파일명 리스트 반환 */
	@PostMapping("/private/images")
	public ResponseEntity uploadImage(
		@RequestParam(value = "image") MultipartFile multipartFile) {
		return ResponseEntity.ok().body(awsS3Service.uploadImage(multipartFile));
	}

	/** @return 성공 시 200 Success */
	@DeleteMapping("/private/images")
	public ResponseEntity<Void> deleteImage(@RequestBody String fileName) {
		awsS3Service.deleteImage(fileName);
		return ResponseEntity.ok().build();
	}
}
