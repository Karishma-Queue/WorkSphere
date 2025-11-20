package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.BoardRequest;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.model.enums.Status;
import com.workify.worksphere.model.value.BoardRequestId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BoardRequestRepository extends JpaRepository<BoardRequest, String> {
    List<BoardRequest> findByRequester(User user);
    List<BoardRequest> findByRequesterAndStatus(User user, Status status);
    List<BoardRequest> findByStatus(Status status);
    Optional<BoardRequest> findByBoardRequestId(BoardRequestId boardRequestId);

}
