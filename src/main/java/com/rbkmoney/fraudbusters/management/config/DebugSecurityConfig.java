package com.rbkmoney.fraudbusters.management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("debug")
public class DebugSecurityConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer(@Value("${cors.allowed-origins}") String[] allowedOrigins) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins(allowedOrigins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }

}

