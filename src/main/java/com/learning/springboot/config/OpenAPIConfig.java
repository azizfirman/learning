package com.learning.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(
            new Info().title("Spring Boot Learning by Firman")
                .description("This is demo project for Spring Boot Learning")
                .contact(new Contact().name("Firman").email("aziz.firman@prosia.co.id"))
        );
    }

}