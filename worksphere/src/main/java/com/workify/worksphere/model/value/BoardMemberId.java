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
public class BoardMemberId implements Serializable {

  @Column(name = "board_member_id", length = 36)
  private String value;

  private BoardMemberId(String value) {
    validate(value);
    this.value = value;
  }

  public static BoardMemberId of(String value) {
    return new BoardMemberId(value);
  }

  public static BoardMemberId generate() {
    return new BoardMemberId(UUID.randomUUID().toString());
  }

  private void validate(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("BoardMemberId cannot be null or blank");
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

