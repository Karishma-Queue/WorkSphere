package com.workify.worksphere.model.enums;

public enum Priority {
  HIGHEST,
  HIGH,
  MEDIUM,
  LOW,
  UNKNOWN;

  public static Priority fromString(String value) {
    try {
      return Priority.valueOf(value);
    } catch (Exception e) {
      return Priority.UNKNOWN;
    }
  }
}
