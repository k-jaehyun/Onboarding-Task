package com.example.backenddevelopmentonboardingtask.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backenddevelopmentonboardingtask.config.TestSecurityConfig;
import com.example.backenddevelopmentonboardingtask.application.UserService;
import com.example.backenddevelopmentonboardingtask.domain.User;
import com.example.backenddevelopmentonboardingtask.domain.UserRoleEnum;
import com.example.backenddevelopmentonboardingtask.infrastructure.security.UserDetailsImpl;
import com.example.backenddevelopmentonboardingtask.presentation.controller.UserController;
import com.example.backenddevelopmentonboardingtask.presentation.dto.request.SignRequestDto;
import com.example.backenddevelopmentonboardingtask.presentation.dto.request.SignupRequestDto;
import com.example.backenddevelopmentonboardingtask.presentation.dto.response.SignResponseDto;
import com.example.backenddevelopmentonboardingtask.presentation.dto.response.SignupResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  private ObjectMapper objectMapper;


  @Test
  @DisplayName("회원가입 테스트")
  void signup() throws Exception {
    SignupRequestDto requestDto = new SignupRequestDto("testuser", "password", "nickname");
    SignupResponseDto responseDto = new SignupResponseDto("testuser", "nickname", List.of("USER"));

    when(userService.signup(any(SignupRequestDto.class))).thenReturn(responseDto);

    mockMvc.perform(post("/api/users/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.nickname").value("nickname"))
        .andExpect(jsonPath("$.authorities").isArray());
  }

  @Test
  @DisplayName("로그인 테스트")
  void sign() throws Exception {
    SignRequestDto requestDto = new SignRequestDto("testuser", "password");
    SignResponseDto responseDto = new SignResponseDto("access-token");

    when(userService.sign(eq("testuser"), eq("password"), any(HttpServletResponse.class)))
        .thenReturn(responseDto);

    mockMvc.perform(post("/api/users/sign")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("access-token"));
  }

  @Test
  @DisplayName("닉네임 조회 테스트")
  void getNickname() throws Exception {
    String nickname = "testnickname";
    UserDetailsImpl userDetails = new UserDetailsImpl(
        new User("testuser", "password", nickname, UserRoleEnum.USER));

    when(userService.getNickname(eq(1L), any(User.class))).thenReturn(nickname);

    mockMvc.perform(get("/api/users/1/nickname")
            .with(user(userDetails)))
        .andExpect(status().isOk())
        .andExpect(content().string(nickname));
  }
}