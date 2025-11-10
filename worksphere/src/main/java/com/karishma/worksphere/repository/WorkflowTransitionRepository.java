package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.WorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, UUID> {
    boolean existsByWorkflow_WorkflowIdAndFromStatus_StatusIdAndToStatus_StatusId(UUID id,UUID id1,UUID id2);
}
