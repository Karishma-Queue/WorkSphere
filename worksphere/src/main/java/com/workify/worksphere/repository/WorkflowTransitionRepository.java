package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.WorkflowTransition;
import com.workify.worksphere.model.value.WorkflowId;
import com.workify.worksphere.model.value.WorkflowStatusId;
import com.workify.worksphere.model.value.WorkflowTransitionId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, String> {
    boolean existsByWorkflow_WorkflowIdAndFromStatus_StatusIdAndToStatus_StatusId(WorkflowId workflowId,
                                                                                  WorkflowStatusId fromStatusId,WorkflowStatusId toStatusId);
        Optional<WorkflowTransition> findByWorkflowTransitionId(WorkflowTransitionId workflowTransitionId);
}
