package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.Issue;
import com.workify.worksphere.model.value.BoardId;
import com.workify.worksphere.model.value.IssueId;
import com.workify.worksphere.model.value.SprintId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, IssueId> {
  List<Issue> findByBoard_BoardId(BoardId boardId);
  Optional<Issue> findByIssueId(IssueId issueId);
  List<Issue> findByBoard_BoardIdAndSprintIsNull(BoardId boardId);
  List<Issue> findBySprint_SprintId(SprintId sprintId);
}
