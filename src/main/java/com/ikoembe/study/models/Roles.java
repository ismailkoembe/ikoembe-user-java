package com.ikoembe.study.models;

public enum Roles {
  ROLE_ADMIN ,
  ROLE_STUDENT,
  ROLE_GUARDIAN,
  ROLE_TEACHER;
  private String name;

  public String getName() {
    return name;
  }
}
