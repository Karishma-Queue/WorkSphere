package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.Board;
import com.karishma.worksphere.model.entity.BoardMember;
import com.karishma.worksphere.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BoardMemberRepository extends JpaRepository<BoardMember, UUID> {
  boolean existsByBoardAndUser(Board board, User user);
 boolean existsByBoardAndBoardMember(Board board,BoardMember boardMember);
 BoardMember findByBoardAndBoardMember(Board board,BoardMember boardMember);
}
