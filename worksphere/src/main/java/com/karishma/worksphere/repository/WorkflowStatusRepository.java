package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowStatusRepository extends JpaRepository<WorkflowStatus, UUID> {
    boolean existsByWorkflow_WorkflowIdAndStatusName(UUID workflowId, String statusName);
   boolean existsByWorkflow_WorkflowIdAndStatusId(UUID id1,UUID id2);
   Optional<WorkflowStatus> findByWorkflow_WorkflowIdAndStatus_StatusId(UUID id1, UUID id2);
}
