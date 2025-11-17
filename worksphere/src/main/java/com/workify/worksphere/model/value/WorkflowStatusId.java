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
public class WorkflowStatusId implements Serializable {

  @Column(name = "workflow_status_id", length = 36, updatable = false, nullable = false)
  private String value;

  private WorkflowStatusId(String value) {
    validate(value);
    this.value = value;
  }

  public static WorkflowStatusId of(String value) {
    return new WorkflowStatusId(value);
  }

  public static WorkflowStatusId generate() {
    return new WorkflowStatusId(UUID.randomUUID().toString());
  }

  private void validate(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("WorkflowStatusId cannot be null or empty");
    }

    try {
      UUID.fromString(value);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid UUID format for WorkflowStatusId: " + value);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
