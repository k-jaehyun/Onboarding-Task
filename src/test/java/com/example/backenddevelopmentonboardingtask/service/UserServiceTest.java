package com.example.backenddevelopmentonboardingtask.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.backenddevelopmentonboardingtask.application.UserService;
import com.example.backenddevelopmentonboardingtask.domain.User;
import com.example.backenddevelopmentonboardingtask.domain.UserRoleEnum;
import com.example.backenddevelopmentonboardingtask.infrastructure.repository.UserRepository;
import com.example.backenddevelopmentonboardingtask.infrastructure.utils.JwtUtil;
import com.example.backenddevelopmentonboardingtask.presentation.dto.request.SignupRequestDto;
import com.example.backenddevelopmentonboardingtask.presentation.dto.response.SignResponseDto;
import com.example.backenddevelopmentonboardingtask.presentation.dto.response.SignupResponseDto;
import com.example.backenddevelopmentonboardingtask.presentation.exception.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @Test
  @DisplayName("회원가입 성공 테스트")
  void signup_success() {
    // given
    SignupRequestDto requestDto = new SignupRequestDto("testuser", "password", "nickname");
    User user = new User("testuser", "encodedPassword", "nickname", UserRoleEnum.USER);
    when(userRepository.existsByUsername(requestDto.username())).thenReturn(false);
    when(passwordEncoder.encode(requestDto.password())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);

    // when
    SignupResponseDto responseDto = userService.signup(requestDto);

    // then
    assertThat(responseDto.username()).isEqualTo("testuser");
    assertThat(responseDto.nickname()).isEqualTo("nickname");
    assertThat(responseDto.authorities()).contains("ROLE_USER");
  }

  @Test
  @DisplayName("회원가입 실패 - 중복 사용자")
  void signup_fail_duplicate_user() {
    // given
    SignupRequestDto requestDto = new SignupRequestDto("testuser", "password", "nickname");
    when(userRepository.existsByUsername(requestDto.username())).thenReturn(true);

    // when & then
    ApiException exception = assertThrows(ApiException.class, () -> userService.signup(requestDto));
    assertThat(exception.getMessage()).isEqualTo("이미 존재하는 사용자입니다.");
  }

  @Test
  @DisplayName("로그인 성공 테스트")
  void sign_success() {
    // given
    User user = new User("testuser", "encodedPassword", "nickname", UserRoleEnum.USER);
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
    when(jwtUtil.createAccessToken("testuser")).thenReturn("access-token");
    when(jwtUtil.createRefreshToken(eq("testuser"), any())).thenReturn("refresh-token");
    HttpServletResponse response = mock(HttpServletResponse.class);

    // when
    SignResponseDto responseDto = userService.sign("testuser", "password", response);

    // then
    assertThat(responseDto.token()).isEqualTo("access-token");
  }

  @Test
  @DisplayName("로그인 실패 - 존재하지 않는 사용자")
  void sign_fail_user_not_found() {
    // given
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

    // when & then
    ApiException exception = assertThrows(ApiException.class, () ->
        userService.sign("testuser", "password", mock(HttpServletResponse.class)));
    assertThat(exception.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
  }

  @Test
  @DisplayName("로그인 실패 - 잘못된 비밀번호")
  void sign_fail_wrong_password() {
    // given
    User user = new User("testuser", "encodedPassword", "nickname", UserRoleEnum.USER);
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

    // when & then
    ApiException exception = assertThrows(ApiException.class, () ->
        userService.sign("testuser", "wrongpassword", mock(HttpServletResponse.class)));
    assertThat(exception.getMessage()).isEqualTo("Invalid Password");
  }

  @Test
  @DisplayName("닉네임 조회 성공 테스트")
  void getNickname_success() {
    // given
    User user = new User("testuser", "password", "nickname", UserRoleEnum.USER);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    // when
    String nickname = userService.getNickname(1L, user);

    // then
    assertThat(nickname).isEqualTo("nickname");
  }

  @Test
  @DisplayName("닉네임 조회 실패 - 존재하지 않는 ID")
  void getNickname_fail_user_not_found() {
    // given
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    ApiException exception = assertThrows(ApiException.class,
        () -> userService.getNickname(1L, new User()));
    assertThat(exception.getMessage()).isEqualTo("존재하지 않는 ID입니다.");
  }
}
