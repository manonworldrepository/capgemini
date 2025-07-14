package com.capgemini.twilight.assessment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Book and Author Management API")
                .version("1.0.0")
                .description(
                    """
                    This is a sample Spring Boot RESTful service using springdoc-openapi to document the API.
                    It provides endpoints to manage authors and their books.
                    """
                )
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://springdoc.org")
                )
            );
    }
}