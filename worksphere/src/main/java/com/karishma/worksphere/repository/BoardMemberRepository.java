package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.Board;
import com.karishma.worksphere.model.entity.BoardMember;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.enums.BoardRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardMemberRepository extends JpaRepository<BoardMember, UUID> {
  boolean existsByBoardAndUser(Board board, User user);
 boolean existsByBoardAndBoardMember(Board board,BoardMember boardMember);
 BoardMember findByBoardAndBoardMember(Board board,BoardMember boardMember);
 List<BoardMember> findByBoardId(UUID id);
    Optional<BoardMember> findByBoard_BoardIdAndBoardRole(UUID boardId, BoardRole boardRole);
   Optional<BoardMember> findByBoard_BoardIdAndBoardMemberId(UUID board_id,UUID id);

}
