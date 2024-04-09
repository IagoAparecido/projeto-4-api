package com.projeto.interdisciplinar.enums;

public enum Roles {
  ADMIN("ADMIN"),
  USER("USER");

  public String role;

  Roles(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }
}