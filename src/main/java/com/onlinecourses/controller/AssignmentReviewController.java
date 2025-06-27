package com.onlinecourses.controller;

import com.onlinecourses.entity.*;
import com.onlinecourses.repository.AssignmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/creator/assignments")
@RequiredArgsConstructor
public class AssignmentReviewController {

    private final AssignmentSubmissionRepository submissionRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllAssignments(@AuthenticationPrincipal User user) {
        List<AssignmentSubmission> all = submissionRepository.findAll();

        List<Map<String, Object>> result = all.stream()
                .filter(s -> s.getAssignmentSection().getTopic().getCourse().getCreator().getId().equals(user.getId()))
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("submissionId", s.getId());
                    map.put("studentId", s.getStudent().getId());
                    map.put("studentName", s.getStudent().getName());
                    map.put("courseTitle", s.getAssignmentSection().getTopic().getCourse().getTitle());
                    map.put("topicTitle", s.getAssignmentSection().getTopic().getTitle());
                    map.put("submittedAt", s.getSubmittedAt());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<?> viewSubmission(@AuthenticationPrincipal User user,
                                            @PathVariable UUID submissionId) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        Course course = submission.getAssignmentSection().getTopic().getCourse();

        if (!course.getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(Map.of(
                "studentName", submission.getStudent().getName(),
                "fileName", submission.getFileName(),
                "fileContent", submission.getFileContent(),
                "submittedAt", submission.getSubmittedAt()
        ));
    }
}
