package com.ikoembe.study.user.models;

import java.security.PrivateKey;

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
