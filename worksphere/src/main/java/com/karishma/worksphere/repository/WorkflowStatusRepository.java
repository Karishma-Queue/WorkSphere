package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowStatusRepository extends JpaRepository<WorkflowStatus, UUID> {
    boolean existsByWorkflow_WorkflowIdAndStatusName(UUID workflowId, String statusName);

}
