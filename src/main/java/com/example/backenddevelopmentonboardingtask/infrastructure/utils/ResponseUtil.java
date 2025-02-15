package com.example.backenddevelopmentonboardingtask.infrastructure.utils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;

public class ResponseUtil {

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
