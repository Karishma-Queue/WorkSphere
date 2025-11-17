package com.workify.worksphere.model.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public final class BoardId {

  @Column(name = "board_id")
  private String value;

  private BoardId(String value) {
    validate(value);
    this.value = value;
  }

  public static BoardId of(String value) {
    return new BoardId(value);
  }

  public static BoardId generate() {
    return new BoardId(UUID.randomUUID().toString());
  }

  private void validate(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("BoardId cannot be null or empty");
    }
    try {
      UUID.fromString(value);
    } catch (Exception e) {
      throw new IllegalArgumentException("BoardId must be a valid UUID: " + value);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
