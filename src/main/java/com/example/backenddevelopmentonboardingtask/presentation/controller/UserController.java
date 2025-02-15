package com.example.backenddevelopmentonboardingtask.presentation.controller;

import com.example.backenddevelopmentonboardingtask.application.UserService;
import com.example.backenddevelopmentonboardingtask.presentation.request.SignupRequestDto;
import com.example.backenddevelopmentonboardingtask.presentation.response.SignupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto) {
    SignupResponseDto signupResponseDto = userService.signup(signupRequestDto);
    return ResponseEntity.ok(signupResponseDto);
  }

}
