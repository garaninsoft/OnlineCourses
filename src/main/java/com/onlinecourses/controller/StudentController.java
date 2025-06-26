package com.onlinecourses.controller;

import com.onlinecourses.dto.CourseProgressResponse;
import com.onlinecourses.entity.Course;
import com.onlinecourses.entity.CourseProgress;
import com.onlinecourses.entity.Role;
import com.onlinecourses.entity.User;
import com.onlinecourses.repository.CourseProgressRepository;
import com.onlinecourses.repository.CourseRepository;
import com.onlinecourses.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final CourseRepository courseRepository;
    private final CourseProgressRepository courseProgressRepository;
    private final TopicRepository topicRepository;

    @GetMapping("/courses")
    public ResponseEntity<?> getAvailableCourses() {
        List<Map<String, Object>> courses = courseRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(course -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", course.getId());
                    map.put("title", course.getTitle());
                    map.put("description", course.getDescription());
                    map.put("topicsCount", course.getTopics().size());
                    map.put("creatorName", course.getCreator().getName());
                    return map;
                })
                .toList();

        return ResponseEntity.ok(courses);
    }

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<?> enroll(@AuthenticationPrincipal User user,
                                    @PathVariable UUID courseId) {

        if (user.getRole() != Role.STUDENT) {
            return ResponseEntity.status(403).build();
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Optional<CourseProgress> existing = courseProgressRepository.findByCourseAndStudent(course, user);
        if (existing.isPresent()) {
            return ResponseEntity.ok(Map.of("message", "Already enrolled"));
        }

        CourseProgress progress = CourseProgress.builder()
                .course(course)
                .student(user)
                .startedAt(java.time.LocalDateTime.now())
                .build();

        courseProgressRepository.save(progress);

        return ResponseEntity.ok(Map.of("message", "Enrolled successfully"));
    }

    @GetMapping("/courses/{courseId}/topics")
    public ResponseEntity<?> getCourseTopics(@AuthenticationPrincipal User user,
                                             @PathVariable UUID courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (user.getRole() != Role.STUDENT) {
            return ResponseEntity.status(403).build();
        }

        List<Map<String, Object>> topics = course.getTopics().stream()
                .sorted(Comparator.comparingInt(t -> t.getOrderIndex() != null ? t.getOrderIndex() : 0))
                .map(topic -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", topic.getId());
                    map.put("title", topic.getTitle());
                    map.put("orderIndex", topic.getOrderIndex());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(topics);
    }

    @GetMapping("/courses/{courseId}/progress")
    public ResponseEntity<?> getProgress(@AuthenticationPrincipal User user,
                                         @PathVariable UUID courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        CourseProgress progress = courseProgressRepository.findByCourseAndStudent(course, user)
                .orElseThrow(() -> new RuntimeException("Not enrolled in course"));

        CourseProgressResponse response = new CourseProgressResponse(
                progress.getId(),
                course.getId(),
                course.getTitle(),
                progress.getStartedAt(),
                progress.getCompletedAt(),
                progress.getCompletedAt() != null
        );

        return ResponseEntity.ok(response);
    }
}
