package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.Workflow;
import com.karishma.worksphere.model.enums.IssueType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {
    List<Workflow> findByBoard_BoardId(UUID id);
    boolean existsByBoardIdAndWorkflowName(UUID id,String name);
    Optional<Workflow> findByBoardIdAndIsDefaultTrue(UUID boardId);
    long countByBoard_BoardId(UUID boardId);
   Workflow findByBoard_BoardIdAndIssueType(UUID id, IssueType issueType);
   Optional<Workflow> findByBoard_BoardIdAndIsDefaultTrue(UUID id);



}
