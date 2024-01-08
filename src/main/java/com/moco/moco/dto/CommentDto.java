package com.moco.moco.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.moco.moco.domain.Comment;
import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;

import lombok.Getter;
import lombok.Setter;

public class CommentDto {

	@Setter
	@Getter
	public static class Request {
		private Long parentId;
		private String content;
		private User user;
		private Post post;
		private Comment parentComment;

		/* DTO -> Entity */
		public Comment toEntity() {
			Comment comments = Comment.builder()
				.content(content)
				.user(user)
				.isRemoved(false)
				.post(post)
				.parent(parentComment)
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
		private String userId;
		private String picture;
		private String name;
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
			this.userId = comment.getUser().getId();
			this.picture = comment.getUser().getPicture();
			this.name = comment.getUser().getName();
			this.postId = comment.getPost().getId();
			this.isRemoved = comment.isRemoved();
		}
	}

	@Getter
	@Setter
	public static class Count {
		private Long postId;
		private Long totalCount;

		public Count(Long postId, Long totalCount) {
			this.postId = postId;
			this.totalCount = totalCount;
		}
	}

}
