package com.moco.moco.dto.queryDslDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.moco.moco.dto.CommentDto;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostDetailVo {
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
	private String contact_method;
	private String link;
	private Integer view;
	private Integer commentCnt;
	private LocalDateTime created_date;
	private boolean isRemoved;
	private boolean isFull;
	private List<CommentDto.Response> comments = new ArrayList<>();
	private String writer;
	private String picture;

	@QueryProjection
	public PostDetailVo(Long id, String title, String content, String type, String capacity, String mode,
		String duration,
		String techStack, String recruitmentPosition, LocalDate deadLine, String contact_method, String link,
		Integer view, Integer commentCnt, LocalDateTime created_date, boolean isRemoved, boolean isFull,
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
		this.contact_method = contact_method;
		this.link = link;
		this.view = view;
		this.commentCnt = commentCnt;
		this.created_date = created_date;
		this.isRemoved = isRemoved;
		this.isFull = isFull;
		this.writer = writer;
		this.picture = picture;
	}
}