package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.WorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, String> {
    boolean existsByWorkflow_WorkflowIdAndFromStatus_StatusIdAndToStatus_StatusId(String id,
                                                                                  String id1,
                                                                                  String id2);
}
