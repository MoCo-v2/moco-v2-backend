package com.moco.moco.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.moco.moco.domain.Comment;
import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CommentDto {

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class Request {
		private Long id;
		private String content;
		private LocalDateTime createdDate;
		private LocalDateTime modifiedDate;
		private User user;
		private Post post;
		private Comment parent;

		/* DTO -> Entity */
		public Comment toEntity() {
			Comment comments = Comment.builder()
				.id(id)
				.content(content)
				.user(user)
				.isRemoved(false)
				.post(post)
				.parent(parent)
				.build();
			return comments;
		}
	}

	@Setter
	@Getter
	public static class Response {
		private Long id;
		private String content;
		private LocalDateTime createdDate;
		private LocalDateTime modifiedDate;
		private String name;
		private String userId;
		private Long postId;
		private Long parentId;
		private List<CommentDto.Response> childList = new ArrayList<>();
		private boolean isRemoved;

		/* Entity -> DTO */
		public Response(Comment comment) {
			this.id = comment.getId();
			this.content = comment.getContent();
			this.createdDate = comment.getCreatedDate();
			this.modifiedDate = comment.getModifiedDate();
			this.name = comment.getUser().getName();
			this.userId = comment.getUser().getId();
			this.postId = comment.getPost().getId();
			this.isRemoved = comment.isRemoved();
		}
	}

}
