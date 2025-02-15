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
        description = "swagger API 입니다"
    )
)
public class SwaggerConfig {
  @Bean
  public OpenAPI openAPI() {
    SecurityScheme apiKey = new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .in(SecurityScheme.In.HEADER)
        .name("Authorization"); // ✅ 여기를 "Authorization"으로 통일

    SecurityRequirement securityRequirement = new SecurityRequirement()
        .addList("Authorization"); // ✅ 여기도 "Authorization"으로 변경

    return new OpenAPI()
        .components(new Components().addSecuritySchemes("Authorization", apiKey)) // ✅ 통일
        .addSecurityItem(securityRequirement)
        .addServersItem(new Server().url("/"));
  }
}