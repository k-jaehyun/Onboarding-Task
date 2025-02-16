package com.example.backenddevelopmentonboardingtask.filter;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backenddevelopmentonboardingtask.infrastructure.security.JwtAuthorizationFilter;
import com.example.backenddevelopmentonboardingtask.infrastructure.security.UserDetailsServiceImpl;
import com.example.backenddevelopmentonboardingtask.infrastructure.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

class JwtAuthorizationFilterTest {

  @InjectMocks
  private JwtAuthorizationFilter jwtAuthorizationFilter;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private UserDetailsServiceImpl userDetailsService;

  @Mock
  private FilterChain filterChain;

  @Mock
  private UserDetails userDetails;

  private final String secretKey = "test-secret-key-for-test-this-is-for-length";
  private final String validToken = Jwts.builder()
      .setSubject("testuser")
      .setExpiration(new Date(System.currentTimeMillis() + 10000))
      .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
      .compact();

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldPassThroughExcludedPaths() throws ServletException, IOException {
    request.setRequestURI("/api/users/sign");
    jwtAuthorizationFilter.doFilter(request, response, filterChain);
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void shouldReturnForbiddenWhenTokenIsMissing() throws ServletException, IOException {
    request.setRequestURI("/api/protected");
    jwtAuthorizationFilter.doFilter(request, response, filterChain);
    assertEquals(403, response.getStatus());
  }

  @Test
  void shouldReturnForbiddenWhenTokenIsInvalid() throws ServletException, IOException {
    request.setRequestURI("/api/protected");
    request.addHeader("Authorization", "Bearer invalid_token");

    when(jwtUtil.validateToken(anyString(), any(), any())).thenReturn(false);

    jwtAuthorizationFilter.doFilter(request, response, filterChain);
    assertEquals(403, response.getStatus());
  }

  @Test
  void shouldAuthenticateWhenTokenIsValid() throws ServletException, IOException {
    request.setRequestURI("/api/protected");
    request.addHeader("Authorization", "Bearer " + validToken);

    Claims claims = Jwts.parserBuilder()
        .setSigningKey(secretKey.getBytes())
        .build()
        .parseClaimsJws(validToken)
        .getBody();

    when(jwtUtil.extractToken(any())).thenReturn(validToken);
    when(jwtUtil.validateToken(anyString(), any(), any())).thenReturn(true);
    when(jwtUtil.getUserInfoFromToken(validToken)).thenReturn(claims);
    when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

    jwtAuthorizationFilter.doFilter(request, response, filterChain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain, times(1)).doFilter(request, response);
  }
}