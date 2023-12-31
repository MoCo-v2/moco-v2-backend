package com.moco.moco.dto.queryDslDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostVo {
	private Long id;
	private String title;
	private String content;
	private String type;
	private String capacity;
	private String mode;
	private String duration;
	private String techStack;
	private String recruitmentPosition;
	private LocalDate deadLine;
	private String contactMethod;
	private String link;
	private Integer view;
	private Integer commentCnt;
	private LocalDateTime createdDate;
	private boolean isRemoved;
	private boolean isFull;
	private String writer;
	private String picture;

	@QueryProjection
	public PostVo(Long id, String title, String content, String type, String capacity, String mode, String duration,
		String techStack, String recruitmentPosition, LocalDate deadLine, String contactMethod, String link,
		Integer view, Integer commentCnt, LocalDateTime createdDate, boolean isRemoved, boolean isFull,
		String writer, String picture) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.type = type;
		this.capacity = capacity;
		this.mode = mode;
		this.duration = duration;
		this.techStack = techStack;
		this.recruitmentPosition = recruitmentPosition;
		this.deadLine = deadLine;
		this.contactMethod = contactMethod;
		this.link = link;
		this.view = view;
		this.commentCnt = commentCnt;
		this.createdDate = createdDate;
		this.isRemoved = isRemoved;
		this.isFull = isFull;
		this.writer = writer;
		this.picture = picture;
	}

	public PostVo() {
	}
}