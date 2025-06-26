package com.onlinecourses.controller;

import com.onlinecourses.dto.UserProfileUpdateRequest;
import com.onlinecourses.entity.Role;
import com.onlinecourses.entity.User;
import com.onlinecourses.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole().name(),
                "creatorRequested", user.isCreatorRequested()
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UserProfileUpdateRequest request) {

        user.setName(request.name());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    @PostMapping("/request-creator")
    public ResponseEntity<Map<String, String>> requestCreatorRole(@AuthenticationPrincipal User user) {
        if (user.getRole() != Role.STUDENT) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Only students can request creator role"));
        }

        user.setCreatorRequested(true);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Creator role request submitted"));
    }
}
