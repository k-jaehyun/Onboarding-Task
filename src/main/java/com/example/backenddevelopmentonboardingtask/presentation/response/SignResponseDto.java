package com.example.backenddevelopmentonboardingtask.presentation.response;

public record SignResponseDto(String token) {

  public static SignResponseDto from(String accessToken) {
    return new SignResponseDto(accessToken);
  }
}
