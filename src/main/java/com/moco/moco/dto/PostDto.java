package com.moco.moco.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class PostDto {

	@Setter
	@Getter
	public static class Request {
		private Long id;
		private String writer;
		@NotBlank(message = "제목을 입력해주세요.")
		private String title;
		private String content;
		private String hashtag;
		private String subcontent;
		private String thumbnail;
		private int view;
		private String location;
		private LocalDateTime createdDate;
		private LocalDateTime modifiedDate;
		private User user;

		/* Dto -> Entity */
		public Post toEntity() {
			return Post.builder()
				.id(id)
				.writer(writer)
				.hashTag(hashtag)
				.title(title)
				.content(content)
				.subcontent(subcontent)
				.thumbnail(thumbnail)
				.view(0)
				.location(location)
				.user(user)
				.build();
		}
	}

	@Setter
	@Getter
	public static class Response {
		private Long id;
		private String title;
		private String writer;
		private String content;
		private String hashtag;
		private String subcontent;
		private String thumbnail;
		private int view;
		private String location;
		private LocalDateTime createdDate;
		private LocalDateTime modifiedDate;
		private String userId;
		private String userImg;
		private List<CommentDto.Response> comments;
		private boolean isfull;

		/* Entity -> Dto */
		public Response(Post post) {
			this.id = post.getId();
			this.title = post.getTitle();
			this.writer = post.getWriter();
			this.content = post.getContent();
			this.hashtag = post.getHashTag();
			this.subcontent = post.getSubcontent();
			this.thumbnail = post.getThumbnail();
			this.view = post.getView();
			this.location = post.getLocation();
			this.createdDate = post.getCreatedDate();
			this.modifiedDate = post.getModifiedDate();
			this.userId = post.getUser().getId();
			this.userImg = post.getUser().getPicture();
			this.comments = post.getComments().stream().map(CommentDto.Response::new).collect(Collectors.toList());
			this.isfull = post.isIsfull();
		}
	}

	@Getter
	@Setter
	public static class PostDetailDto {
		private PostDto.Response postDto;
		private List<CommentDto.Response> comments;
		private Long likeCount;
		private Long joinUsersCount;

		public PostDetailDto(PostDto.Response postDto, List<CommentDto.Response> comments, Long likeCount,
			Long joinUsersCount) {
			this.postDto = postDto;
			this.comments = comments;
			this.likeCount = likeCount;
			this.joinUsersCount = joinUsersCount;
		}
	}

	@Getter
	@Setter
	public static class Posts {
		private List<PostListVo> posts;
		private Long total;

		public Posts(List<PostListVo> posts, Long total) {
			this.posts = posts;
			this.total = total;
		}
	}

}

