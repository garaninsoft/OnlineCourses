package com.onlinecourses.security;

import com.onlinecourses.service.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthFilter authFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)    // CSRF не нужен для API
                .authorizeHttpRequests(auth -> auth
                        // всегда пропускаем preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // разрешаем регистрацию/логин
                        .requestMatchers("/api/auth/**", "/auth/**").permitAll()
                        // всё остальное под авторизацию
                        .anyRequest().authenticated()
                )
                // наш фильтр авторизации по Token
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
