package com.example.backenddevelopmentonboardingtask.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Swagger API",
        version = "v1",
        description = "Swagger API 입니다"
    )
)
public class SwaggerConfig {

  private static final String SECURITY_SCHEME_NAME = "Authorization";

  /**
   * OpenAPI Bean 설정
   * - JWT 인증을 위한 Security Scheme 설정 포함
   * - 기본 서버 URL 설정
   */
  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, createSecurityScheme()))
        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
        .addServersItem(new Server().url("/"));
  }

  /**
   * JWT 인증을 위한 Security Scheme 생성
   * @return SecurityScheme 설정 객체
   */
  private SecurityScheme createSecurityScheme() {
    return new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .in(SecurityScheme.In.HEADER)
        .name(SECURITY_SCHEME_NAME);
  }
}