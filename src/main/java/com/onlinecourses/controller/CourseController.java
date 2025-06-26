package com.onlinecourses.controller;

import com.onlinecourses.dto.CourseResponse;
import com.onlinecourses.entity.Course;
import com.onlinecourses.entity.Role;
import com.onlinecourses.entity.User;
import com.onlinecourses.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseRepository courseRepository;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(course -> new CourseResponse(
                        course.getId(),
                        course.getTitle(),
                        course.getDescription(),
                        course.getCreator().getName(),
                        course.getCreatedAt(),
                        course.getTopics().size()
                ))
                .toList();

        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseDetails(@PathVariable UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Map<String, Object> response = Map.of(
                "id", course.getId(),
                "title", course.getTitle(),
                "description", course.getDescription(),
                "creatorName", course.getCreator().getName(),
                "createdAt", course.getCreatedAt(),
                "topicsCount", course.getTopics().size()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCourse(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request) {

        if (user.getRole() != Role.CREATOR) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Only creators can create courses"));
        }

        Course course = Course.builder()
                .title(request.get("title"))
                .description(request.get("description"))
                .creator(user)
                .build();

        courseRepository.save(course);

        return ResponseEntity.ok(Map.of(
                "id", course.getId(),
                "message", "Course created successfully"
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateCourse(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "You can only edit your own courses"));
        }

        course.setTitle(request.get("title"));
        course.setDescription(request.get("description"));
        courseRepository.save(course);

        return ResponseEntity.ok(Map.of("message", "Course updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCourse(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "You can only delete your own courses"));
        }

        courseRepository.delete(course);

        return ResponseEntity.ok(Map.of("message", "Course deleted successfully"));
    }

    @GetMapping("/my")
    public ResponseEntity<List<CourseResponse>> getMyCourses(@AuthenticationPrincipal User user) {
        if (user.getRole() != Role.CREATOR) {
            return ResponseEntity.status(403).build();
        }

        List<CourseResponse> courses = courseRepository.findByCreator(user).stream()
                .map(course -> new CourseResponse(
                        course.getId(),
                        course.getTitle(),
                        course.getDescription(),
                        course.getCreator().getName(),
                        course.getCreatedAt(),
                        course.getTopics().size()
                ))
                .toList();

        return ResponseEntity.ok(courses);
    }
}
