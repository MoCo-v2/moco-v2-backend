package com.moco.moco.service.post;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moco.moco.domain.Post;
import com.moco.moco.domain.User;
import com.moco.moco.dto.CommentDto;
import com.moco.moco.dto.PostDto;
import com.moco.moco.dto.PostListVo;
import com.moco.moco.repository.PostRepository;
import com.moco.moco.repository.PostRepositoryCustom;
import com.moco.moco.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PostService {

	private UserRepository userRepository;
	private PostRepository postRepository;
	private PostRepositoryCustom postRepositoryCustom;

	private CommentService commentService;
	private LikeService likeService;
	private RecruitService recruitService;

	private static final int PAGE_POST_COUNT = 9; // 한 페이지에 존재하는 게시글 수

	/* 모집중인 게시글 가져오기 */
	@Transactional(readOnly = true)
	public List<PostListVo> getPostListOnRecruit(Integer pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum - 1, PAGE_POST_COUNT,
			Sort.by(Sort.Direction.DESC, "created_date"));
		List<PostListVo> postList = postRepositoryCustom.getPostsOnRecruit(pageRequest);
		return postList;
	}

	/* 모든 게시글 가져오기 */
	@Transactional(readOnly = true)
	public PostDto.Posts getPosts(Integer offset, Integer limit) {
		PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "created_date"));
		List<PostListVo> posts = postRepositoryCustom.getPosts(pageRequest);
		Long total = getPostCount();

		return new PostDto.Posts(posts, total);
	}

	/* 게시글 검색 */
	@Transactional
	public PostDto.Posts searchPosts(Integer offset, Integer limit, String keyword) {
		PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "created_date"));
		List<PostListVo> posts = postRepositoryCustom.getSearchPost(pageRequest, keyword);
		Long total = getPostCount();
		return new PostDto.Posts(posts, total);
	}

	/* 내가 쓴글 가져오기 */
	@Transactional(readOnly = true)
	public List<PostListVo> getMyPosts(Integer pageNum, Long userId) {
		PageRequest pageRequest = PageRequest.of(pageNum - 1, PAGE_POST_COUNT,
			Sort.by(Sort.Direction.DESC, "created_date"));
		return postRepositoryCustom.getMyPosts(pageRequest, userId);
	}

	/* 게시글 상세 */
	@Transactional
	public PostDto.PostDetailDto getPost(Long postId) {
		Post post = postRepository.findByIdWithFetchJoin(postId);
		PostDto.Response postDto = new PostDto.Response(post);

		/* get comment to NestedStructure */
		List<CommentDto.Response> comments = commentService.convertNestedStructure(post.getComments());

		/* get like count */
		Long likeCount = likeService.findLikeCount(postId);

		/* get Number of currently registered users  */
		Long joinUsers = recruitService.countToJoinUsers(postId);

		/* view update +1 */
		postRepository.updateView(postId);

		return new PostDto.PostDetailDto(postDto, comments, likeCount, joinUsers);
	}

	@Transactional(readOnly = true)
	public Post findById(Long postId) {
		return postRepository.findByIdWithFetchJoin(postId);
	}

	/* 게시글 저장 */
	@Transactional
	public Long savePost(Long userId, PostDto.Request postDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
		postDto.setUser(user);

		return postRepository.save(postDto.toEntity()).getId();
	}

	/* 게시글 수정 */
	@Transactional
	public Long updatePost(Long postId, PostDto.Request postDto) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
		post.update(postDto.getTitle(), postDto.getHashtag(), postDto.getContent(), postDto.getSubcontent(),
			postDto.getThumbnail(), postDto.getLocation());
		return post.getId();
	}

	/* UPDATE -  모집 마감 */
	@Transactional
	public boolean updateFull(Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
		return post.close();
	}

	/* DELETE - 게시글 삭제 */
	@Transactional
	public void deletePost(Long id) {
		postRepository.deleteById(id);
	}

	/* 페이징 */
	@Transactional
	public Long getPostCount() {
		return postRepository.count();
	}

	public Integer getPageList(Integer curPageNum) {
		// 총 게시글 갯수
		Double postsTotalCount = Double.valueOf(this.getPostCount());

		// 총 게시글 기준으로 계산한 마지막 페이지 번호 계산 (올림으로 계산)
		return (Integer)(int)(Math.ceil((postsTotalCount / PAGE_POST_COUNT)));
	}

	/* 조회수 */
	@Transactional
	public int updateView(Long id) {
		return postRepository.updateView(id);
	}

}