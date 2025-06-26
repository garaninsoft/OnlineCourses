package com.onlinecourses.controller;

import com.onlinecourses.entity.*;
import com.onlinecourses.repository.CourseRepository;
import com.onlinecourses.repository.CourseProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/creator")
@RequiredArgsConstructor
public class CreatorController {

    private final CourseRepository courseRepository;
    private final CourseProgressRepository courseProgressRepository;

    @GetMapping("/courses")
    public ResponseEntity<?> getMyCourses(@AuthenticationPrincipal User user) {
        if (user.getRole() != Role.CREATOR) {
            return ResponseEntity.status(403).build();
        }

        List<Map<String, Object>> courses = courseRepository.findAllByCreator(user).stream()
                .map(course -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", course.getId());
                    map.put("title", course.getTitle());
                    map.put("description", course.getDescription());
                    map.put("studentsCount", course.getCourseProgresses().size());
                    return map;
                })
                .toList();

        return ResponseEntity.ok(courses);
    }

    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<?> getStudents(@AuthenticationPrincipal User user,
                                         @PathVariable UUID courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        List<Map<String, Object>> students = course.getCourseProgresses().stream()
                .map(progress -> {
                    User student = progress.getStudent();
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentId", student.getId());
                    map.put("name", student.getName());
                    map.put("email", student.getEmail());
                    map.put("startedAt", progress.getStartedAt());
                    map.put("completedAt", progress.getCompletedAt());
                    return map;
                })
                .toList();

        return ResponseEntity.ok(students);
    }

    @GetMapping("/courses/{courseId}/students/{studentId}/progress")
    public ResponseEntity<?> getStudentProgress(@AuthenticationPrincipal User user,
                                                @PathVariable UUID courseId,
                                                @PathVariable UUID studentId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        CourseProgress progress = courseProgressRepository.findByCourseAndStudent(
                        course,
                        User.builder().id(studentId).build())
                .orElseThrow(() -> new RuntimeException("Progress not found"));

        return ResponseEntity.ok(Map.of(
                "studentId", progress.getStudent().getId(),
                "name", progress.getStudent().getName(),
                "startedAt", progress.getStartedAt(),
                "completedAt", progress.getCompletedAt()
        ));
    }
}
