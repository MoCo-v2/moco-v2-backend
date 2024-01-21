package com.moco.moco.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
	name = "bookmark",
	uniqueConstraints = {
		@UniqueConstraint(name = "unique_user_post", columnNames = {"user_id", "post_id"})
	}
)
@Entity
public class Bookmark {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@Column(updatable = false, name = "postCreatedDate")
	private LocalDateTime postCreatedDate;

	@Builder
	public Bookmark(User user, Post post, LocalDateTime postCreatedDate) {
		this.user = user;
		this.post = post;
		this.postCreatedDate = postCreatedDate;
	}
}
