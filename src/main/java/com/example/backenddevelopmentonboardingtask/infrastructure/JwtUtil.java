package com.example.backenddevelopmentonboardingtask.infrastructure;

import com.example.backenddevelopmentonboardingtask.presentation.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String AUTHORIZATION_KEY = "auth";
  public static final String BEARER_PREFIX = "Bearer ";
  private final long ACCESS_TOKEN_TIME = 30 * 60 * 1000L; // 30 minutes
  private final long REFRESH_TOKEN_TIME = 24 * 60 * 60 * 1000L; // 24 hours

  @Value("${jwt.secret.key}")
  private String secretKey;
  private Key key;
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  public String createAccessToken(String username) {
    Date date = new Date();

    return BEARER_PREFIX +
        Jwts.builder()
            .setSubject(username)
            .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME))
            .setIssuedAt(date)
            .signWith(key, signatureAlgorithm)
            .compact();
  }

  public String createRefreshToken(String username, String role) {
    Date date = new Date();

    return BEARER_PREFIX +
        Jwts.builder()
            .setSubject(username)
            .claim(AUTHORIZATION_KEY, role)
            .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
            .setIssuedAt(date)
            .signWith(key, signatureAlgorithm)
            .compact();
  }

  public void addJwtToCookie(String token, HttpServletResponse res) {
    try {
      token = URLEncoder.encode(token, "utf-8")
          .replaceAll("\\+", "%20");

      Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token);
      cookie.setPath("/");

      res.addCookie(cookie);
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage());
    }
  }

  public String substringToken(String tokenValue) {
    if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
      return tokenValue.substring(7);
    }
    throw new ApiException("Not Found Token", HttpStatus.BAD_REQUEST);
  }

  public boolean validateToken(String token, HttpServletResponse response) throws IOException {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException | MalformedJwtException | SignatureException e) {
      sendErrorResponse(response, "Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.",
          HttpStatus.FORBIDDEN);
    } catch (ExpiredJwtException e) {
      sendErrorResponse(response, "Expired JWT token, 만료된 JWT token 입니다.", HttpStatus.FORBIDDEN);
    } catch (UnsupportedJwtException e) {
      sendErrorResponse(response, "Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.",
          HttpStatus.FORBIDDEN);
    } catch (IllegalArgumentException e) {
      sendErrorResponse(response, "JWT claims is empty, 잘못된 JWT 토큰 입니다.", HttpStatus.FORBIDDEN);
    }
    return false;
  }

  public Claims getUserInfoFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  public String getTokenFromRequest(HttpServletRequest req) {
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
          try {
            return URLDecoder.decode(cookie.getValue(), "UTF-8");
          } catch (UnsupportedEncodingException e) {
            return null;
          }
        }
      }
    }
    return null;
  }

  public static void sendErrorResponse(HttpServletResponse res, String message, HttpStatus status)
      throws IOException {
    res.setStatus(status.value());
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    res.getWriter().write(String.format(
        "{\"message\": \"%s\", \"status\": %d}",
        message,
        status.value()
    ));
  }
}