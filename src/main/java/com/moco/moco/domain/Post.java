package com.moco.moco.domain;

// JPA에서는 프록시 객체 생성을 위해 파라미터가 없는 기본 생성자를 반드시 하나를 생성해야 한다.
// 파라미터 없는 생성자 생성 어노테이션인 NoArgsConstructor를 사용하고 기본 생성자를 사용하는 곳은 JPA Entity Class 밖에 없기 때문에 Protect로 접근을 제한한다.

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moco.moco.dto.PostDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "post")
public class Post extends Time {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //PK 생성 규칙
	private Long id;

	@Column(length = 500, nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Column
	private String type;

	@Column
	private String capacity;

	@Column
	private String mode;

	@Column
	private String duration;

	@Column
	private String techStack;

	@Column
	private String recruitmentPosition;

	@Column
	private LocalDate deadLine;

	@Column
	private String contact_method;

	@Column
	private String link;

	@Column
	private boolean isRemoved = false;

	@Column
	private boolean isFull = false;

	@Column(columnDefinition = "integer default 0", nullable = false)
	private int commentCnt;

	@Column(columnDefinition = "integer default 0", nullable = false)
	private int view;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	/* 댓글 */
	@OrderBy("id asc") //댓글 정렬
	@JsonIgnoreProperties({"post"})
	@OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Comment> comments;

	@Builder
	public Post(Long id, String title, String content, String type, String capacity, String mode, String duration,
		String techStack, String recruitmentPosition, LocalDate deadLine, String contact_method, String link,
		boolean isRemoved, boolean isFull, int commentCnt, int view, User user, List<Comment> comments) {
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
		this.isRemoved = isRemoved;
		this.isFull = isFull;
		this.commentCnt = commentCnt;
		this.view = view;
		this.user = user;
		this.comments = comments;
	}

	public Long update(PostDto.Request postDto) {
		this.title = postDto.getTitle();
		this.content = postDto.getContent();
		this.type = postDto.getType();
		this.capacity = postDto.getCapacity();
		this.mode = postDto.getMode();
		this.duration = postDto.getDuration();
		this.techStack = postDto.getTechStack();
		this.deadLine = postDto.getDeadLine();
		this.recruitmentPosition = postDto.getRecruitmentPosition();
		this.contact_method = postDto.getContact_method();
		this.link = postDto.getLink();
		return this.id;
	}
}








