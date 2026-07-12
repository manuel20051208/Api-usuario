package com.example.apiusuario.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiUsuarioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Usuario")
                        .version("v1")
                        .description("Documentacion OpenAPI para usuarios, tareas, sueno, eventos y resumen."))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT en formato Bearer.")));
    }
}
