package com.onlinecourses.controller;

import com.onlinecourses.dto.AuthResponse;
import com.onlinecourses.dto.LoginRequest;
import com.onlinecourses.dto.RegisterRequest;
import com.onlinecourses.entity.Role;
import com.onlinecourses.entity.User;
import com.onlinecourses.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .email(request.email())
                .name(request.name())
                .passwordHash(encoder.encode(request.password()))
                .role(Role.STUDENT)
                .authToken(UUID.randomUUID().toString())
                .creatorRequested(false)
                .build();

        userRepository.save(user);

        return new AuthResponse(user.getAuthToken(), user.getRole().name());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.getAuthToken() == null) {
            user.setAuthToken(UUID.randomUUID().toString());
            userRepository.save(user);
        }

        return new AuthResponse(user.getAuthToken(), user.getRole().name());
    }
}
