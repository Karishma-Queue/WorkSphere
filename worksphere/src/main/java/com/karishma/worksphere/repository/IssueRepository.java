package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue,String> {
    List<Issue> findByBoard_BoardId(String boardId);
}
