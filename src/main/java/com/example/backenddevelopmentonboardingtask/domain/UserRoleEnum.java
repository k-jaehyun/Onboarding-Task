package com.example.backenddevelopmentonboardingtask.domain;

public enum UserRoleEnum {

  USER(Authority.USER),
  ADMIN(Authority.ADMIN);

  private final String authorityName;

  UserRoleEnum(String authority) {
    this.authorityName = authority;
  }

  public String getAuthorityName() {
    return this.authorityName;
  }

  public static class Authority {
    public static final String USER = "ROLE_USER";
    public static final String ADMIN = "ROLE_ADMIN";
  }
}