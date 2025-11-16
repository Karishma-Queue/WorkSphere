package com.workify.worksphere.model.enums;

public enum Status {
  PENDING,
  APPROVED,
  REJECTED,
  CANCELLED,
  UNKNOWN;

  public static Status fromString(String value) {
    try {
      return Status.valueOf(value);
    } catch (Exception e) {
      return Status.UNKNOWN;
    }
  }
}
