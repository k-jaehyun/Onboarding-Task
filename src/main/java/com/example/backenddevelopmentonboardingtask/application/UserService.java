package com.example.backenddevelopmentonboardingtask.application;

import com.example.backenddevelopmentonboardingtask.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

}
