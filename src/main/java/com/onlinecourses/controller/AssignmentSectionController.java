package com.onlinecourses.controller;

import com.onlinecourses.dto.AssignmentSectionRequest;
import com.onlinecourses.entity.AssignmentSection;
import com.onlinecourses.entity.Topic;
import com.onlinecourses.entity.User;
import com.onlinecourses.repository.AssignmentSectionRepository;
import com.onlinecourses.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/topics/{topicId}/assignment")
@RequiredArgsConstructor
public class AssignmentSectionController {

    private final TopicRepository topicRepository;
    private final AssignmentSectionRepository assignmentSectionRepository;

    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal User user,
                                    @PathVariable UUID topicId,
                                    @RequestBody @Valid AssignmentSectionRequest request) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        AssignmentSection section = new AssignmentSection();
        section.setDescription(request.description());
        section.setOrderIndex(request.orderIndex());
        section.setTopic(topic);

        assignmentSectionRepository.save(section);
        topic.setAssignmentSection(section);
        topicRepository.save(topic);

        return ResponseEntity.ok(Map.of("message", "Assignment section created"));
    }

    @PutMapping
    public ResponseEntity<?> update(@AuthenticationPrincipal User user,
                                    @PathVariable UUID topicId,
                                    @RequestBody @Valid AssignmentSectionRequest request) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        AssignmentSection section = topic.getAssignmentSection();
        section.setDescription(request.description());
        section.setOrderIndex(request.orderIndex());
        assignmentSectionRepository.save(section);

        return ResponseEntity.ok(Map.of("message", "Assignment section updated"));
    }

    @GetMapping
    public ResponseEntity<?> get(@PathVariable UUID topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        AssignmentSection section = topic.getAssignmentSection();
        return ResponseEntity.ok(Map.of(
                "id", section.getId(),
                "description", section.getDescription(),
                "orderIndex", section.getOrderIndex()
        ));
    }
}
