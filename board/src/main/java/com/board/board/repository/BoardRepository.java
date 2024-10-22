package com.board.board.repository;

import com.board.board.domain.Board;
import com.board.board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByTitleContaining(String keyword);

    Board findByUser(User user);
}
