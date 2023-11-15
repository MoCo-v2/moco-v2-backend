package com.board.board.service.post;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.board.domain.Post;
import com.board.board.domain.User;
import com.board.board.dto.PostDto;
import com.board.board.dto.PostListVo;
import com.board.board.repository.PostRepository;
import com.board.board.repository.PostRepositoryCustom;
import com.board.board.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PostService {

	private UserRepository userRepository;
	private PostRepository boardRepository;
	private PostRepositoryCustom boardRepositoryCustom;

	private static final int PAGE_POST_COUNT = 9; // 한 페이지에 존재하는 게시글 수

	/* 모집중인 게시글 가져오기 */
	@Transactional(readOnly = true)
	public List<PostListVo> getBoardListOnRecruit(Integer pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum - 1, PAGE_POST_COUNT,
			Sort.by(Sort.Direction.DESC, "created_date"));
		List<PostListVo> boardList = boardRepositoryCustom.getPostsOnRecruit(pageRequest);
		return boardList;
	}

	/* 모든 게시글 가져오기 */
	@Transactional(readOnly = true)
	public List<PostListVo> getBoards(Integer offset, Integer limit) {
		PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "created_date"));

		return boardRepositoryCustom.getPosts(pageRequest);
	}

	/* 내가 쓴글 가져오기 */
	@Transactional(readOnly = true)
	public List<PostListVo> getMyBoardList(Integer pageNum, Long userId) {
		PageRequest pageRequest = PageRequest.of(pageNum - 1, PAGE_POST_COUNT,
			Sort.by(Sort.Direction.DESC, "created_date"));
		List<PostListVo> boardList = boardRepositoryCustom.getMyPosts(pageRequest, userId);
		return boardList;
	}

	/* 게시글 검색 */
	@Transactional
	public List<PostListVo> searchPosts(Integer pageNum, String keyword) {
		PageRequest pageRequest = PageRequest.of(pageNum - 1, PAGE_POST_COUNT,
			Sort.by(Sort.Direction.DESC, "created_date"));
		List<PostListVo> boardList = boardRepositoryCustom.getSearchPost(pageRequest, keyword);
		return boardList;
	}

	/* 게시글 수정페이지 */
	@Transactional(readOnly = true)
	public PostDto.Response getPost(Long BoardId) {
		Post board = boardRepository.findById(BoardId)
			.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
		return new PostDto.Response(board);
	}

	/* 게시글 상세 */
	@Transactional(readOnly = true)
	public PostDto.Response findById(Long BoardId) {
		Post board = boardRepository.findByIdWithFetchJoin(BoardId);
		return new PostDto.Response(board);
	}

	/* 게시글 저장 */
	@Transactional
	public Long savePost(String name, PostDto.Request boardDto) {
		User user = userRepository.findByName(name);
		boardDto.setUser(user);
		return boardRepository.save(boardDto.toEntity()).getId();
	}

	/* 게시글 수정 */
	@Transactional
	public Long updatePost(Long board_id, PostDto.Request boardDto) {
		Post board = boardRepository.findById(board_id)
			.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
		board.update(boardDto.getTitle(), boardDto.getHashtag(), boardDto.getContent(), boardDto.getSubcontent(),
			boardDto.getThumbnail(), boardDto.getLocation());
		return board.getId();
	}

	/* UPDATE -  모집 마감 */
	@Transactional
	public boolean updateFull(Long boardId) {
		Post board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
		return board.close();
	}

	/* DELETE - 게시글 삭제 */
	@Transactional
	public void deletePost(Long id) {
		boardRepository.deleteById(id);
	}

	/* 페이징 */
	@Transactional
	public Long getBoardCount() {
		return boardRepository.count();
	}

	public Integer getPageList(Integer curPageNum) {
		// 총 게시글 갯수
		Double postsTotalCount = Double.valueOf(this.getBoardCount());

		// 총 게시글 기준으로 계산한 마지막 페이지 번호 계산 (올림으로 계산)
		Integer totalLastPageNum = (int)(Math.ceil((postsTotalCount / PAGE_POST_COUNT)));
		return totalLastPageNum;
	}

	/* 조회수 */
	@Transactional
	public int updateView(Long id) {
		return boardRepository.updateView(id);
	}

}