package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {
    List<Workflow> findByBoard_BoardId(UUID id);
}
