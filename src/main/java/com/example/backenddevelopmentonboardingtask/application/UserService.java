package com.example.backenddevelopmentonboardingtask.application;

import com.example.backenddevelopmentonboardingtask.domain.User;
import com.example.backenddevelopmentonboardingtask.domain.UserRoleEnum;
import com.example.backenddevelopmentonboardingtask.infrastructure.repository.UserRepository;
import com.example.backenddevelopmentonboardingtask.presentation.exception.ApiException;
import com.example.backenddevelopmentonboardingtask.presentation.request.SignupRequestDto;
import com.example.backenddevelopmentonboardingtask.presentation.response.SignupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public SignupResponseDto signup(SignupRequestDto requestDto) {

    String username = requestDto.username();
    String password = passwordEncoder.encode(requestDto.password());
    String nickname = requestDto.nickname();

    if (userRepository.existsByUsername(username)) {
      throw new ApiException("이미 존재하는 사용자입니다.", HttpStatus.BAD_REQUEST);
    }

    User user = new User(username, password, nickname, UserRoleEnum.USER);
    User savedUser = userRepository.save(user);

    return SignupResponseDto.from(savedUser);
  }
}
