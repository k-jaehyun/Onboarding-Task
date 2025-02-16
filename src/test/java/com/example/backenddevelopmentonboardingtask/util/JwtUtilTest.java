package com.example.backenddevelopmentonboardingtask.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.backenddevelopmentonboardingtask.domain.UserRoleEnum;
import com.example.backenddevelopmentonboardingtask.infrastructure.utils.JwtUtil;
import com.example.backenddevelopmentonboardingtask.presentation.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtUtilTest {
  private static JwtUtil jwtUtil;
  private static final String SECRET_KEY = "kevin12341234123412341234123412341234";
  private static final String BASE64_SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());

  @BeforeAll
  public void init() {
    jwtUtil = new JwtUtil();

    ReflectionTestUtils.setField(jwtUtil, "secretKey", BASE64_SECRET_KEY);
    jwtUtil.init();
  }

  @Test
  public void generateAccessTokenTest() {
    String accessToken = jwtUtil.createAccessToken("testUser");

    System.out.println("Access Token: " + accessToken);
    assertThat(accessToken, notNullValue());
  }

  @Test
  public void generateRefreshTokenTest() {
    String refreshToken = jwtUtil.createRefreshToken("testUser", UserRoleEnum.USER.getAuthorityName());

    System.out.println("Refresh Token: " + refreshToken);
    assertThat(refreshToken, notNullValue());
  }

  @Test
  public void validateValidTokenTest() throws IOException {
    String accessToken = jwtUtil.createAccessToken("testUser");
    boolean isValid = jwtUtil.validateToken(jwtUtil.substringToken(accessToken), new MockHttpServletResponse(), new MockHttpServletRequest());

    assertThat(isValid, is(true));
  }

  @Test
  public void validateExpiredTokenTest() throws IOException {
    // 현재 시간보다 이전의 만료 시간을 가진 토큰 생성
    Date now = new Date();
    Date expiredDate = new Date(now.getTime() - 1000L); // 1초 전 만료

    Key key = (Key) ReflectionTestUtils.getField(jwtUtil, "key");

    String expiredToken = "Bearer " + Jwts.builder()
        .setSubject("testUser")
        .setExpiration(expiredDate)
        .setIssuedAt(now)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();

    boolean isValid = jwtUtil.validateToken(jwtUtil.substringToken(expiredToken), response, request);

    assertThat(isValid, is(false));
    assertThat(response.getStatus(), is(HttpStatus.FORBIDDEN.value()));
  }

  @Test
  public void extractUserInfoFromTokenTest() {
    String accessToken = jwtUtil.createAccessToken("testUser");
    Claims claims = jwtUtil.getUserInfoFromToken(jwtUtil.substringToken(accessToken));

    assertThat(claims.getSubject(), is("testUser"));
  }

  @Test
  public void addJwtToCookieTest() {
    MockHttpServletResponse response = new MockHttpServletResponse();
    String token = jwtUtil.createRefreshToken("testUser", UserRoleEnum.USER.getAuthorityName());

    jwtUtil.addJwtToCookie(token, response);

    assertThat(response.getCookies().length, is(1));
    assertThat(response.getCookies()[0].getName(), is(JwtUtil.AUTHORIZATION_HEADER));
  }

  @Test
  public void extractTokenFromCookieTest() {
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();
    String token = jwtUtil.createRefreshToken("testUser", UserRoleEnum.USER.getAuthorityName());

    jwtUtil.addJwtToCookie(token, response);

    request.setCookies(response.getCookies());

    String extractedToken = jwtUtil.getRefreshToken(request);

    assertThat(extractedToken, is(token));
  }

  @Test
  public void regenerateAccessTokenTest() {
    String refreshToken = jwtUtil.createRefreshToken("testUser", UserRoleEnum.USER.getAuthorityName());
    String newAccessToken = jwtUtil.regenerateAccessToken(refreshToken);

    assertThat(newAccessToken, notNullValue());
  }

  @Test
  public void regenerateAccessToken_InvalidToken_ThrowsException() {
    String invalidRefreshToken = "invalid.token.value";

    ApiException exception = assertThrows(ApiException.class, () -> {
      jwtUtil.regenerateAccessToken(invalidRefreshToken);
    });

    System.out.println("Exception Message: " + exception.getMessage());

    assertThat(exception.getMessage(), is("토큰에서 유저 정보 조회 실패"));
  }
}