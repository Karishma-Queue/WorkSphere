package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.Board;
import com.karishma.worksphere.model.entity.BoardMember;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.enums.BoardRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardMemberRepository extends JpaRepository<BoardMember, String> {

    boolean existsByBoardAndUser(Board board, User user);

    Optional<BoardMember> findByBoardAndUser(Board board, User user);

    List<BoardMember> findByBoard_BoardId(String boardId);

    Optional<BoardMember> findByBoard_BoardIdAndBoardMemberId(  String boardId, String memberId);
    List<BoardMember> findByUser_UserIdAndBoardRole(String user_id,BoardRole role);
    Optional<BoardMember> findByBoard_BoardIdAndBoardRole(String boardId, BoardRole boardRole);
}
