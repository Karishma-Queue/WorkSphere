package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.Board;
import com.workify.worksphere.model.value.BoardId;
import com.workify.worksphere.model.value.UserId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, String> {
    List<Board> findByCreatedBy_UserId(UserId userId);
    @Query("""
           SELECT b FROM Board b 
           WHERE LOWER(b.boardName) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(b.boardKey) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%'))
           """)
    List<Board> searchBoards(@Param("query") String query);
       Optional<Board> findById(BoardId boardId);
    Optional<Board> findByBoardId(BoardId boardId);
}
