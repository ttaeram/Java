package com.board.board.service;

import com.board.board.domain.Board;
import com.board.board.dto.BoardDto;
import com.board.board.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BoardService {

    private BoardRepository boardRepository;

    private static final int BLOCK_PAGE_NUM_COUNT = 5;

    private static final int PAGE_POST_COUNT = 4;

    private BoardDto convertEntityToDto(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .createdDate(board.getCreatedDate())
                .modifiedDate(board.getModifiedDate())
                .build();
    }

    public List<BoardDto> getBoardlist(Integer pageNum) {
        Page<Board> page = boardRepository.findAll(PageRequest.of(pageNum - 1, PAGE_POST_COUNT, Sort.by(Sort.Direction.ASC, "createdDate")));

        List<Board> boardEntities = page.getContent();
        List<BoardDto> boardDtoList = new ArrayList<>();

        for (Board board : boardEntities) {
            boardDtoList.add(this.convertEntityToDto(board));
        }

        return boardDtoList;
    }

    @Transactional
    public BoardDto getPost(Long id) {
        Optional<Board> boardWrapper = boardRepository.findById(id);
        Board board = boardWrapper.get();

        BoardDto boardDto = BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .createdDate(board.getCreatedDate())
                .modifiedDate(board.getModifiedDate())
                .build();

        return boardDto;
    }

    @Transactional
    public Long savePost(BoardDto boardDto) {
        return boardRepository.save(boardDto.toEntity()).getId();
    }

    @Transactional
    public void deletePost(Long id) {
        boardRepository.deleteById(id);
    }

    @Transactional
    public List<BoardDto> searchPosts(String keyword) {
        List<Board> boardEntities = boardRepository.findByTitleContaining(keyword);
        List<BoardDto> boardDtoList = new ArrayList<>();

        if (boardEntities.isEmpty()) {
            return boardDtoList;
        }

        for (Board board : boardEntities) {
            boardDtoList.add(this.convertEntityToDto(board));
        }

        return boardDtoList;
    }

    @Transactional
    public Long getBoardCount() {
        return boardRepository.count();
    }

    public Integer[] getPageList(Integer curPageNum) {
        Integer[] pageList = new Integer[BLOCK_PAGE_NUM_COUNT];

        Double postsTotalCount = Double.valueOf(this.getBoardCount());

        Integer totalLastPageNum = (int)
                (Math.ceil(postsTotalCount / PAGE_POST_COUNT));

        Integer blockLastPageNum = (totalLastPageNum > curPageNum + BLOCK_PAGE_NUM_COUNT)
                ? curPageNum + BLOCK_PAGE_NUM_COUNT
                : totalLastPageNum;

        curPageNum = (curPageNum <= 3) ? 1 : curPageNum - 2;

        for (int val = curPageNum, idx = 0; val <= blockLastPageNum; val++, idx++) {
            pageList[idx] = val;
        }

        return pageList;
    }
}
