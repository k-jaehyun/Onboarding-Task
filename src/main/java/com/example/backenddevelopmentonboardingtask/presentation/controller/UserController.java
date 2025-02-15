package com.example.backenddevelopmentonboardingtask.presentation.controller;

import com.example.backenddevelopmentonboardingtask.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

}
