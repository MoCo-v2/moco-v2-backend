package com.moco.moco.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;
import com.moco.moco.dto.queryDslDto.PostVo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class PostDto {

	@Setter
	@Getter
	public static class Request {
		@NotBlank(message = "제목을 입력해주세요.")
		private String title;
		private String content;
		private String type;
		private String capacity;
		private String mode;
		private String duration;
		private String techStack;
		private LocalDate deadLine;
		private String recruitmentPosition;
		private String contactMethod;
		private String link;
		private User user;

		/* Dto -> Entity */
		public Post toEntity() {
			return Post.builder()
				.title(title)
				.content(content)
				.type(type)
				.capacity(capacity)
				.mode(mode)
				.duration(duration)
				.techStack(techStack)
				.recruitmentPosition(recruitmentPosition)
				.deadLine(deadLine)
				.contactMethod(contactMethod)
				.link(link)
				.isFull(false)
				.isRemoved(false)
				.commentCnt(0)
				.view(0)
				.user(user)
				.build();
		}

	}

	@Getter
	@Setter
	public static class Response {
		private List<PostVo> posts;
		private int totalPages;
		private Long totalElements;

		public Response(Page<PostVo> posts) {
			this.posts = posts.getContent();
			this.totalPages = posts.getTotalPages();
			this.totalElements = posts.getTotalElements();
		}
	}

}

