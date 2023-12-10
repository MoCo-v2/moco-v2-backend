package com.moco.moco.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "comment")
@Entity
public class Comment extends Time {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parent; //부모댓글 id 참조 (Self Join)

	/* 부모 댓글을 삭제해도 자식 댓글은 남아있음 */
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	private List<Comment> childList;

	private boolean isRemoved;

	public void update(String content) {
		this.content = content;
	}

	public void remove() {
		this.isRemoved = true;
	}

	@Builder
	public Comment(Long id, String content, Post post, User user, Comment parent, List<Comment> childList,
		boolean isRemoved) {
		this.id = id;
		this.content = content;
		this.post = post;
		this.user = user;
		this.parent = parent;
		this.childList = childList;
		this.isRemoved = isRemoved;
	}
}













