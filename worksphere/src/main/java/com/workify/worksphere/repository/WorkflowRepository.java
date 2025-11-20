package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.Workflow;
import com.workify.worksphere.model.enums.IssueType;
import com.workify.worksphere.model.value.BoardId;
import com.workify.worksphere.model.value.WorkflowId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowRepository extends JpaRepository<Workflow, String> {
    List<Workflow> findByBoard_BoardId(BoardId boardId);
    boolean existsByBoard_BoardIdAndWorkflowName(BoardId boardId, String workflowName);
    long countByBoard_BoardId(BoardId boardId);
    Optional<Workflow> findByWorkflowId(WorkflowId workflowId);
    Workflow findByBoard_BoardIdAndIssueType(BoardId boardId, IssueType issueType);
    Optional<Workflow> findByBoard_BoardIdAndIsDefaultTrue(BoardId boardId);
}
