package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.BoardRequest;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BoardRequestRepository extends JpaRepository<BoardRequest, String> {
    List<BoardRequest> findByRequester(User user);
    List<BoardRequest> findByRequesterAndStatus(User user, Status status);
    List<BoardRequest> findByStatus(Status status);
}
