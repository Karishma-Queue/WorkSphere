package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.Issue;
import com.workify.worksphere.model.value.BoardId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue,String> {
    List<Issue> findByBoard_BoardId(String boardId);
  List<Issue> findByBoard_BoardIdAndSprintIsNull(String BoardId);
}
