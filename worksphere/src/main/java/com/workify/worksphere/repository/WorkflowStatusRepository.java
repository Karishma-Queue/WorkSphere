package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowStatusRepository extends JpaRepository<WorkflowStatus, String> {
    boolean existsByWorkflow_WorkflowIdAndStatusName(String workflowId, String statusName);
    boolean existsByWorkflow_WorkflowIdAndStatusId(String id1, String id2);
    Optional<WorkflowStatus> findByWorkflow_WorkflowIdAndStatusId(String id1, String id2);
    Optional<WorkflowStatus> findByWorkflow_WorkflowIdAndIsInitialTrue(String id);
}
