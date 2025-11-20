package com.workify.worksphere.model.entity;

import com.workify.worksphere.model.value.WorkflowTransitionId;
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

  @EmbeddedId private WorkflowTransitionId workflowTransitionId;

  @PrePersist
  private void prePersist() {
    if (this.workflowTransitionId == null) {
      this.workflowTransitionId = WorkflowTransitionId.generate();
    }
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "workflow_id", nullable = false)
  private Workflow workflow;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_status")
  private WorkflowStatus fromStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_status")
  private WorkflowStatus toStatus;

  @ElementCollection(fetch = FetchType.EAGER)
  @Column(name = "role")
  private Set<String> allowedRoles;
}
