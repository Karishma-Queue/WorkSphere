package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID> {
    List<Issue> findByBoard_BoardId(UUID boardId);


}
