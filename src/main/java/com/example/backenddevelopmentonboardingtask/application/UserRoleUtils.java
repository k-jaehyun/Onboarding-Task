package com.example.backenddevelopmentonboardingtask.application;

import com.example.backenddevelopmentonboardingtask.domain.User;
import com.example.backenddevelopmentonboardingtask.domain.UserRoleEnum;

public class UserRoleUtils {
  private UserRoleUtils() {
  }

  public static String getHighestAuthority(User user) {
    boolean isAdmin = user.getAuthorities().stream()
        .anyMatch(UserRoleEnum.ADMIN::equals);

    return isAdmin ? UserRoleEnum.ADMIN.getAuthorityName() : UserRoleEnum.USER.getAuthorityName();
  }
}