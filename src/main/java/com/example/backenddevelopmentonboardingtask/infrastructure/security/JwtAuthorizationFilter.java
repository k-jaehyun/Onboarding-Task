package com.example.backenddevelopmentonboardingtask.infrastructure.security;

import com.example.backenddevelopmentonboardingtask.infrastructure.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserDetailsServiceImpl userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
      FilterChain filterChain) throws ServletException, IOException {

    String path = req.getRequestURI();
    if (path.equals("/api/users/signup") || path.equals("/api/users/sign")) {
      filterChain.doFilter(req, res);
      return;
    }

    String tokenValue = jwtUtil.extractToken(req);

    if (StringUtils.hasText(tokenValue)) {
      tokenValue = jwtUtil.substringToken(tokenValue);

      if (!jwtUtil.validateToken(tokenValue, res)) {
        String refreshToken = jwtUtil.getRefreshToken(req);
        if (refreshToken == null) {
          return;
        } else {
          tokenValue = jwtUtil.regenerateAccessToken(refreshToken);
        }
      }

      Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
      setAuthentication(info.getSubject());
    } else {
      JwtUtil.sendErrorResponse(res, "Token is missing", HttpStatus.FORBIDDEN);
      return;
    }

    filterChain.doFilter(req, res);
  }

  public void setAuthentication(String username) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Authentication authentication = createAuthentication(username);
    context.setAuthentication(authentication);

    SecurityContextHolder.setContext(context);
  }

  private Authentication createAuthentication(String username) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }
}