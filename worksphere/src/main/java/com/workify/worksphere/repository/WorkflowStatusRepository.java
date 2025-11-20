package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.WorkflowStatus;
import com.workify.worksphere.model.value.WorkflowId;
import com.workify.worksphere.model.value.WorkflowStatusId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowStatusRepository extends JpaRepository<WorkflowStatus, String> {
    boolean existsByWorkflow_WorkflowIdAndStatusName(WorkflowId workflowId, String statusName);
    boolean existsByWorkflow_WorkflowIdAndStatusId(WorkflowId workflowId, WorkflowStatusId statusId);
    Optional<WorkflowStatus> findByWorkflow_WorkflowIdAndStatusId(WorkflowId workflowId, WorkflowStatusId statusId);
    Optional<WorkflowStatus> findByWorkflow_WorkflowIdAndIsInitialTrue(WorkflowId workflowId);
  Optional<WorkflowStatus> findByStatusId(WorkflowStatusId workflowStatusId);
}
