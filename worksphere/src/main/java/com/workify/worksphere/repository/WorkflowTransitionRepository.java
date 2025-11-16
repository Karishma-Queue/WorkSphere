package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.WorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, String> {
    boolean existsByWorkflow_WorkflowIdAndFromStatus_StatusIdAndToStatus_StatusId(String workflowId,
                                                                                  String fromStatusId,
                                                                                  String toStatusId);
}
