package com.karishma.worksphere.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowTransition {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private String workTransitionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="workflow_id", nullable = false)
    Workflow workflow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="from_status")
    private WorkflowStatus fromStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="to_status")
    private WorkflowStatus toStatus;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "role")
    private Set<String> allowedRoles;
}
