package com.onlinecourses.controller;

import com.onlinecourses.entity.*;
import com.onlinecourses.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/student/courses/{courseId}/topics/{topicId}/assignment")
@RequiredArgsConstructor
public class AssignmentSubmissionController {

    private final TopicRepository topicRepository;
    private final AssignmentSubmissionRepository submissionRepository;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@AuthenticationPrincipal User user,
                                    @PathVariable UUID courseId,
                                    @PathVariable UUID topicId,
                                    @RequestParam("file") MultipartFile file) throws IOException {

        if (user.getRole() != Role.STUDENT) {
            return ResponseEntity.status(403).body(Map.of("error", "Only students can submit assignments"));
        }

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getId().equals(courseId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Topic does not belong to course"));
        }

        AssignmentSection section = topic.getAssignmentSection();
        if (section == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No assignment section in this topic"));
        }

        Optional<AssignmentSubmission> existing = submissionRepository.findByAssignmentSectionAndStudent(section, user);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Assignment already submitted"));
        }

        AssignmentSubmission submission = AssignmentSubmission.builder()
                .assignmentSection(section)
                .student(user)
                .fileName(file.getOriginalFilename())
                .fileContent(new String(file.getBytes()))
                .build();

        submissionRepository.save(submission);

        return ResponseEntity.ok(Map.of("message", "Assignment submitted successfully"));
    }
}
