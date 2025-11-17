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
public class IssueId implements Serializable {

  @Column(name = "issue_id", length = 36)
  private String value;

  private IssueId(String value) {
    validate(value);
    this.value = value;
  }

  public static IssueId of(String value) {
    return new IssueId(value);
  }

  public static IssueId generate() {
    return new IssueId(UUID.randomUUID().toString());
  }

  private void validate(String value) {
    if (value == null || value.isBlank())
      throw new IllegalArgumentException("IssueId cannot be null or empty");

    try {
      UUID.fromString(value);
    } catch (Exception e) {
      throw new IllegalArgumentException("IssueId must be a valid UUID: " + value);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
