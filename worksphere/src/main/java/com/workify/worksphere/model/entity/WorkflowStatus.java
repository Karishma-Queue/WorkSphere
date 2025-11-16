package com.workify.worksphere.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowStatus {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private String statusId;

    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    Workflow workflow;

    @Column(name = "status_name", nullable = false)
    private String statusName;

    @Column(name = "started", nullable = false)
    private Boolean started = false;

    @Column(name = "ended", nullable = false)
    private Boolean ended = false;

    @Column(name = "is_initial", nullable = false)
    @Builder.Default
    private Boolean isInitial = false;
}
