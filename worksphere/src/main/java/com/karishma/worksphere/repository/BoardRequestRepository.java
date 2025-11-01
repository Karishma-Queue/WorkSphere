package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.BoardRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BoardRequestRepository extends JpaRepository <BoardRequest, UUID> {
}
