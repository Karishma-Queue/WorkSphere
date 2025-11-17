package com.workify.worksphere.model.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class BoardMemberId {

  private final String value;

  private BoardMemberId(String value) {
    this.value = value;
  }

  public static BoardMemberId of(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("BoardMemberId cannot be null or empty");
    }
    return new BoardMemberId(value);
  }

  public static BoardMemberId newId() {
    return new BoardMemberId(java.util.UUID.randomUUID().toString());
  }

  @Override
  public String toString() {
    return value;
  }
}
