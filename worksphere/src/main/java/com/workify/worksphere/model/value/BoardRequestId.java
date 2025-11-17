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
public final class BoardRequestId {

  @Column(name = "board_request_id")
  private String value;

  private BoardRequestId(String value) {
    validate(value);
    this.value = value;
  }

  public static BoardRequestId of(String value) {
    return new BoardRequestId(value);
  }

  public static BoardRequestId generate() {
    return new BoardRequestId(UUID.randomUUID().toString());
  }

  private void validate(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("BoardRequestId cannot be null or empty");
    }
    try {
      UUID.fromString(value);
    } catch (Exception e) {
      throw new IllegalArgumentException("BoardRequestId must be a valid UUID: " + value);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
