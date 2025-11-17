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
public class UserId implements Serializable {

  @Column(name = "user_id", length = 36)
  private String value;

  private UserId(String value) {
    validate(value);
    this.value = value;
  }

  public static UserId of(String value) {
    return new UserId(value);
  }

  public static UserId generate() {
    return new UserId(UUID.randomUUID().toString());
  }

  private void validate(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("UserId cannot be null or empty");
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
