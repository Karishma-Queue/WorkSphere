package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.Issue;
import com.workify.worksphere.model.entity.Sprint;
import com.workify.worksphere.model.value.BoardId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue,String> {
    List<Issue> findByBoard_BoardId(String boardId);
  List<Issue> findByBoard_BoardIdAndSprintIsNull(String BoardId);
  Optional<Issue> findByIssueId(String issueId);
  List<Issue> findBySprint(Sprint sprint);
}
