package com.workify.worksphere.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.workify.worksphere.model.value.WorkflowStatusId;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowStatus {

  @EmbeddedId
  private WorkflowStatusId statusId;

  @PrePersist
  private void prePersist() {
    if (this.statusId == null) {
      this.statusId = WorkflowStatusId.generate();
    }
  }

  @ManyToOne
  @JoinColumn(name = "workflow_id", nullable = false)
  private Workflow workflow;

  @Column(name = "status_name", nullable = false)
  private String statusName;

  @Column(nullable = false)
  private Boolean started = false;

  @Column(nullable = false)
  private Boolean ended = false;

  @Builder.Default
  @Column(name = "is_initial", nullable = false)
  private Boolean isInitial = false;
}
