package com.example.backenddevelopmentonboardingtask.application;

import com.example.backenddevelopmentonboardingtask.domain.User;
import com.example.backenddevelopmentonboardingtask.domain.UserRoleEnum;
import com.example.backenddevelopmentonboardingtask.infrastructure.repository.UserRepository;
import com.example.backenddevelopmentonboardingtask.infrastructure.utils.JwtUtil;
import com.example.backenddevelopmentonboardingtask.presentation.dto.request.SignupRequestDto;
import com.example.backenddevelopmentonboardingtask.presentation.dto.response.SignResponseDto;
import com.example.backenddevelopmentonboardingtask.presentation.dto.response.SignupResponseDto;
import com.example.backenddevelopmentonboardingtask.presentation.exception.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

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

  public SignResponseDto sign(String username, String password, HttpServletResponse response) {

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ApiException("존재하지 않는 사용자입니다.", HttpStatus.BAD_REQUEST));

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new ApiException("Invalid Password", HttpStatus.BAD_REQUEST);
    }

    String role = UserRoleUtils.getHighestAuthority(user);

    String accessToken = jwtUtil.createAccessToken(username);
    String refreshToken = jwtUtil.createRefreshToken(username, role);

    jwtUtil.addJwtToCookie(refreshToken, response);

    return SignResponseDto.from(accessToken);
  }

  public String getNickname(Long id, User user) {
    User findUser = userRepository.findById(id)
        .orElseThrow(() -> new ApiException("존재하지 않는 ID입니다.", HttpStatus.BAD_REQUEST));

    if (!UserRoleUtils.getHighestAuthority(user).equals(UserRoleEnum.ADMIN.getAuthorityName())
        && !findUser.getUsername().equals(user.getUsername())
    ) {
      throw new ApiException("본인의 닉네임만 조회 가능합니다.", HttpStatus.BAD_REQUEST);
    }

    return findUser.getNickname();
  }
}
