package com.moco.moco.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
		private List<PostList> posts;
		private int totalPages;
		private Long totalElements;

		public Response(List<PostList> posts, int totalPages, Long totalElements) {
			this.posts = posts;
			this.totalPages = totalPages;
			this.totalElements = totalElements;
		}
	}

	@Getter
	@Setter
	public static class PostList {
		private Long id;
		private String title;
		private String type;
		private String capacity;
		private String mode;
		private String duration;
		private String techStack;
		private String recruitmentPosition;
		private LocalDate deadLine;
		private String contactMethod;
		private boolean isFull;
		private Integer view;
		private Integer commentCnt;
		private LocalDateTime createdDate;
		private String userId;
		private String writer;
		private String picture;

		public PostList(PostVo postVo) {
			this.id = postVo.getId();
			this.title = postVo.getTitle();
			this.type = postVo.getType();
			this.capacity = postVo.getCapacity();
			this.mode = postVo.getMode();
			this.duration = postVo.getDuration();
			this.techStack = postVo.getTechStack();
			this.recruitmentPosition = postVo.getRecruitmentPosition();
			this.deadLine = postVo.getDeadLine();
			this.contactMethod = postVo.getContactMethod();
			this.isFull = postVo.isFull();
			this.view = postVo.getView();
			this.commentCnt = postVo.getCommentCnt();
			this.createdDate = postVo.getCreatedDate();
			this.userId = postVo.getUserId();
			this.writer = postVo.getWriter();
			this.picture = postVo.getPicture();
		}
	}

}

