package com.example.backenddevelopmentonboardingtask.presentation.controller;

import com.example.backenddevelopmentonboardingtask.application.UserService;
import com.example.backenddevelopmentonboardingtask.infrastructure.security.UserDetailsImpl;
import com.example.backenddevelopmentonboardingtask.infrastructure.security.UserDetailsServiceImpl;
import com.example.backenddevelopmentonboardingtask.presentation.dto.request.SignRequestDto;
import com.example.backenddevelopmentonboardingtask.presentation.dto.request.SignupRequestDto;
import com.example.backenddevelopmentonboardingtask.presentation.dto.response.SignResponseDto;
import com.example.backenddevelopmentonboardingtask.presentation.dto.response.SignupResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @PostMapping("/sign")
  public ResponseEntity<SignResponseDto> sign(
      @RequestBody SignRequestDto request, HttpServletResponse response) {

    SignResponseDto signResponseDto = userService.sign(
        request.username(), request.password(), response);

    return ResponseEntity.ok(signResponseDto);
  }

  @GetMapping("/{id}/nickname")
  public ResponseEntity<String> getNickname(
      @PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {

    String nickname = userService.getNickname(id, userDetails.getUser());

    return ResponseEntity.ok(nickname);
  }

}
