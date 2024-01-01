package com.moco.moco.service.aws;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.moco.moco.exception.CustomAuthenticationException;
import com.moco.moco.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import marvin.image.MarvinImage;

@RequiredArgsConstructor
@Service
public class AwsS3Service {
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;
	private final String cloudfrontUrl = "https://img.moco.run/";
	private final String dir = "/images";
	private final AmazonS3 amazonS3;

	public String uploadImage(MultipartFile multipartFile) {
		if (!multipartFile.getContentType().contains("image")) {
			throw new CustomAuthenticationException(ErrorCode.BAD_REQUEST);
		}

		String fileName = createFileName(multipartFile.getOriginalFilename());
		String fileFormatName = multipartFile.getContentType()
			.substring(multipartFile.getContentType().lastIndexOf("/") + 1);

		MultipartFile resizedFile = resizeImage(fileName, fileFormatName, multipartFile, 768);

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(resizedFile.getSize());
		objectMetadata.setContentType(multipartFile.getContentType());

		try (InputStream inputStream = resizedFile.getInputStream()) {
			amazonS3.putObject(new PutObjectRequest(bucket + dir, fileName, inputStream, objectMetadata)
				.withCannedAcl(CannedAccessControlList.PublicRead));
			fileName = cloudfrontUrl + fileName;
		} catch (IOException e) {
			throw new CustomAuthenticationException(ErrorCode.SERVER_ERROR);
		}

		return fileName;
	}

	public void deleteImage(String fileName) {
		amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
	}

	private String createFileName(String fileName) {
		return UUID.randomUUID().toString().concat(getFileExtension(fileName));
	}

	private String getFileExtension(String fileName) {
		try {
			return fileName.substring(fileName.lastIndexOf("."));
		} catch (StringIndexOutOfBoundsException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
		}
	}

	private MultipartFile resizeImage(String fileName, String fileFormatName, MultipartFile originalImage,
		int targetWidth) {
		try {
			// MultipartFile -> BufferedImage Convert
			BufferedImage image = ImageIO.read(originalImage.getInputStream());
			// newWidth : newHeight = originWidth : originHeight
			int originWidth = image.getWidth();
			int originHeight = image.getHeight();

			// origin 이미지가 resizing될 사이즈보다 작을 경우 resizing 작업 안 함
			if (originWidth < targetWidth)
				return originalImage;

			MarvinImage imageMarvin = new MarvinImage(image);

			Scale scale = new Scale();
			scale.load();
			scale.setAttribute("newWidth", targetWidth);
			scale.setAttribute("newHeight", targetWidth * originHeight / originWidth);
			scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

			BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(imageNoAlpha, fileFormatName, baos);
			baos.flush();

			return new MockMultipartFile(fileName, baos.toByteArray());

		} catch (IOException e) {
			throw new CustomAuthenticationException(ErrorCode.SERVER_ERROR);
		}
	}
}
