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
@EqualsAndHashCode
@NoArgsConstructor
public final class SprintId implements Serializable {

  @Column(name = "sprint_id")
  private String value;

  private SprintId(String value) {
    validate(value);
    this.value = value;
  }

  // Create SprintId from existing string
  public static SprintId of(String value) {
    return new SprintId(value);
  }

  // Generate a new random SprintId
  public static SprintId generate() {
    return new SprintId(UUID.randomUUID().toString());
  }

  private void validate(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("SprintId cannot be null or empty");
    }
    try {
      UUID.fromString(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("SprintId must be a valid UUID: " + value);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
