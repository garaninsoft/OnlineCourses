package com.onlinecourses.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Разрешить для всех путей
                        .allowedOrigins("http://localhost:5173") // URL Vue dev-сервера
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}
