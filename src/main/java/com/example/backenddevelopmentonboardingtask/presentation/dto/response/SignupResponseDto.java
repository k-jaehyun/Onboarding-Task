package com.example.backenddevelopmentonboardingtask.presentation.dto.response;

import com.example.backenddevelopmentonboardingtask.domain.User;
import com.example.backenddevelopmentonboardingtask.domain.UserRoleEnum;
import java.util.List;
import java.util.stream.Collectors;

public record SignupResponseDto(String username, String nickname, List<String> authorities) {
  public static SignupResponseDto from(User user) {
    return new SignupResponseDto(
        user.getUsername(),
        user.getNickname(),
        user.getAuthorities().stream()
            .map(UserRoleEnum::getAuthorityName)
            .collect(Collectors.toList())
    );
  }
}