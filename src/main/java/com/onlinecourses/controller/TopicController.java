package com.onlinecourses.controller;

import com.onlinecourses.dto.TopicCreateRequest;
import com.onlinecourses.dto.TopicResponse;
import com.onlinecourses.entity.Course;
import com.onlinecourses.entity.Role;
import com.onlinecourses.entity.Topic;
import com.onlinecourses.entity.User;
import com.onlinecourses.repository.CourseRepository;
import com.onlinecourses.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/topics")
@RequiredArgsConstructor
public class TopicController {

    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;

    @GetMapping
    public ResponseEntity<List<TopicResponse>> getTopics(@PathVariable UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<TopicResponse> topics = topicRepository.findByCourseOrderByOrderIndexAsc(course).stream()
                .map(topic -> new TopicResponse(
                        topic.getId(),
                        topic.getTitle(),
                        topic.getOrderIndex(),
                        topic.getTextSection() != null,
                        topic.getVideoSection() != null,
                        topic.getTestSection() != null,
                        topic.getAssignmentSection() != null
                ))
                .toList();

        return ResponseEntity.ok(topics);
    }

    @GetMapping("/{topicId}")
    public ResponseEntity<Map<String, Object>> getTopicDetails(
            @PathVariable UUID courseId,
            @PathVariable UUID topicId) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getId().equals(courseId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Topic does not belong to this course"));
        }

        Map<String, Object> response = Map.of(
                "id", topic.getId(),
                "title", topic.getTitle(),
                "orderIndex", topic.getOrderIndex(),
                "hasText", topic.getTextSection() != null,
                "hasVideo", topic.getVideoSection() != null,
                "hasTest", topic.getTestSection() != null,
                "hasAssignment", topic.getAssignmentSection() != null
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTopic(
            @AuthenticationPrincipal User user,
            @PathVariable UUID courseId,
            @RequestBody TopicCreateRequest request) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "You can only edit your own courses"));
        }

        Topic topic = Topic.builder()
                .title(request.title())
                .orderIndex(request.orderIndex())
                .course(course)
                .build();

        topicRepository.save(topic);

        return ResponseEntity.ok(Map.of(
                "id", topic.getId(),
                "message", "Topic created successfully"
        ));
    }

    @PutMapping("/{topicId}")
    public ResponseEntity<Map<String, String>> updateTopic(
            @AuthenticationPrincipal User user,
            @PathVariable UUID courseId,
            @PathVariable UUID topicId,
            @RequestBody TopicCreateRequest request) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getId().equals(courseId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Topic does not belong to this course"));
        }

        if (!topic.getCourse().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "You can only edit your own courses"));
        }

        topic.setTitle(request.title());
        topic.setOrderIndex(request.orderIndex());
        topicRepository.save(topic);

        return ResponseEntity.ok(Map.of("message", "Topic updated successfully"));
    }

    @DeleteMapping("/{topicId}")
    public ResponseEntity<Map<String, String>> deleteTopic(
            @AuthenticationPrincipal User user,
            @PathVariable UUID courseId,
            @PathVariable UUID topicId) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getId().equals(courseId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Topic does not belong to this course"));
        }

        if (!topic.getCourse().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "You can only edit your own courses"));
        }

        topicRepository.delete(topic);

        return ResponseEntity.ok(Map.of("message", "Topic deleted successfully"));
    }

    @PutMapping("/{topicId}/reorder")
    public ResponseEntity<Map<String, String>> reorderTopic(
            @AuthenticationPrincipal User user,
            @PathVariable UUID courseId,
            @PathVariable UUID topicId,
            @RequestBody Map<String, Integer> request) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "You can only edit your own courses"));
        }

        topic.setOrderIndex(request.get("orderIndex"));
        topicRepository.save(topic);

        return ResponseEntity.ok(Map.of("message", "Topic reordered successfully"));
    }
}
