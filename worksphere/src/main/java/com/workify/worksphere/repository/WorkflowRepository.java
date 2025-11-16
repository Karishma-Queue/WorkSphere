package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.Workflow;
import com.workify.worksphere.model.enums.IssueType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowRepository extends JpaRepository<Workflow, String> {
    List<Workflow> findByBoard_BoardId(String id);
    boolean existsByBoard_BoardIdAndWorkflowName(String id, String name);
    long countByBoard_BoardId(String boardId);
    Workflow findByBoard_BoardIdAndIssueType(String id, IssueType issueType);
    Optional<Workflow> findByBoard_BoardIdAndIsDefaultTrue(String id);
}
