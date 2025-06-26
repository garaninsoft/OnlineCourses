package com.onlinecourses.controller;

import com.onlinecourses.dto.CreatorRequestDto;
import com.onlinecourses.entity.Role;
import com.onlinecourses.entity.User;
import com.onlinecourses.repository.CourseRepository;
import com.onlinecourses.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @GetMapping("/creator-requests")
    public ResponseEntity<List<CreatorRequestDto>> getCreatorRequests(@AuthenticationPrincipal User admin) {
        if (admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        List<CreatorRequestDto> requests = userRepository.findAll().stream()
                .filter(user -> user.isCreatorRequested() && user.getRole() == Role.STUDENT)
                .map(user -> new CreatorRequestDto(user.getId(), user.getName(), user.getEmail()))
                .toList();

        return ResponseEntity.ok(requests);
    }

    @PostMapping("/approve-creator/{userId}")
    public ResponseEntity<Map<String, String>> approveCreator(
            @AuthenticationPrincipal User admin,
            @PathVariable UUID userId) {

        if (admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.STUDENT || !user.isCreatorRequested()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid request"));
        }

        user.setRole(Role.CREATOR);
        user.setCreatorRequested(false);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Creator role approved"));
    }

    @DeleteMapping("/reject-creator/{userId}")
    public ResponseEntity<Map<String, String>> rejectCreator(
            @AuthenticationPrincipal User admin,
            @PathVariable UUID userId) {

        if (admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setCreatorRequested(false);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Creator request rejected"));
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers(@AuthenticationPrincipal User admin) {
        if (admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(user -> Map.<String, Object>of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "name", user.getName(),
                        "role", user.getRole().name(),
                        "creatorRequested", user.isCreatorRequested()
                ))
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/creators")
    public ResponseEntity<List<Map<String, Object>>> getAllCreators(@AuthenticationPrincipal User admin) {
        if (admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        List<Map<String, Object>> creators = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.CREATOR)
                .map(user -> Map.<String, Object>of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "name", user.getName(),
                        "coursesCount", user.getCreatedCourses().size()
                ))
                .toList();

        return ResponseEntity.ok(creators);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Map<String, Object>>> getAllCourses(@AuthenticationPrincipal User admin) {
        if (admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        List<Map<String, Object>> courses = courseRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(course -> Map.<String, Object>of(
                        "id", course.getId(),
                        "title", course.getTitle(),
                        "description", course.getDescription(),
                        "creatorName", course.getCreator().getName(),
                        "createdAt", course.getCreatedAt(),
                        "topicsCount", course.getTopics().size()
                ))
                .toList();

        return ResponseEntity.ok(courses);
    }
}
