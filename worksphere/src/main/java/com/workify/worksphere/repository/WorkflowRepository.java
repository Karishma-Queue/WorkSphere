package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.Workflow;
import com.workify.worksphere.model.enums.IssueType;
import com.workify.worksphere.model.value.BoardId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowRepository extends JpaRepository<Workflow, String> {
    List<Workflow> findByBoard_BoardId(BoardId boardId);
    boolean existsByBoard_BoardIdAndWorkflowName(String boardId, String workflowName);
    long countByBoard_BoardId(String boardId);
    Workflow findByBoard_BoardIdAndIssueType(String boardId, IssueType issueType);
    Optional<Workflow> findByBoard_BoardIdAndIsDefaultTrue(String boardId);
}
