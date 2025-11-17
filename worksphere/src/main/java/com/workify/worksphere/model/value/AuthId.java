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
public class AuthId implements Serializable {

  @Column(name = "auth_id")
  private String value;

  private AuthId(String value) {
    validate(value);
    this.value = value;
  }

  public static AuthId of(String value) {
    return new AuthId(value);
  }

  public static AuthId generate() {
    return new AuthId(UUID.randomUUID().toString());
  }

  private void validate(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("AuthId cannot be null or empty");
    }

    try {
      UUID.fromString(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("AuthId must be a valid UUID format: " + value);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}