package com.workify.worksphere.model.enums;

public enum IssueType {
  EPIC,
  STORY,
  TASK,
  BUG,
  SUB_TASK,
  UNKNOWN;

  public static IssueType fromString(String value) {
    try {
      return IssueType.valueOf(value);
    } catch (Exception e) {
      return IssueType.UNKNOWN;
    }
  }
}


