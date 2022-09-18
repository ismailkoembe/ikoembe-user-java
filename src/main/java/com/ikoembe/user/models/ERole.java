package com.ikoembe.user.models;

public enum ERole {
  ROLE_ADMIN,
  ROLE_STUDENT,
  ROLE_GUARDIAN,
  ROLE_TEACHER;
  private String name;

  public String getName() {
    return name;
  }
}
