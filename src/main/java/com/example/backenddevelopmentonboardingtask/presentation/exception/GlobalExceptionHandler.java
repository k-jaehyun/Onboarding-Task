package com.example.backenddevelopmentonboardingtask.presentation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity apiExceptionHandler(ApiException ex) {
    return ResponseEntity.status(ex.getStatus()).body(ex.getMsg());
  }

}
