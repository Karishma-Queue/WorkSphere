package com.karishma.worksphere.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowStatus {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID status_id;
    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    Workflow workflow;
    @Column(name = "status_name", nullable = false)
    private String statusName;
}
