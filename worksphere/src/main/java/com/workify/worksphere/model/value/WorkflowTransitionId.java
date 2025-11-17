package com.workify.worksphere.model.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class WorkflowTransitionId implements Serializable {

  @Column(name = "workflow_transition_id", length = 36, nullable = false, updatable = false)
  private String value;

  private WorkflowTransitionId(String value) {
    validate(value);
    this.value = value;
  }

  public static WorkflowTransitionId of(String value) {
    return new WorkflowTransitionId(value);
  }

  public static WorkflowTransitionId generate() {
    return new WorkflowTransitionId(UUID.randomUUID().toString());
  }

  private void validate(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("WorkflowTransitionId cannot be null or empty");
    }
    try {
      UUID.fromString(value);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid UUID format: " + value);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
