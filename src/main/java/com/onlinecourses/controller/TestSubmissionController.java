package com.onlinecourses.controller;

import com.onlinecourses.dto.TestSubmissionRequest;
import com.onlinecourses.entity.*;
import com.onlinecourses.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student/courses/{courseId}/topics/{topicId}/test")
@RequiredArgsConstructor
public class TestSubmissionController {

    private final TopicRepository topicRepository;
    private final AnswerRepository answerRepository;
    private final TestResultRepository testResultRepository;
    private final CourseProgressRepository courseProgressRepository;

    @PostMapping("/submit")
    public ResponseEntity<?> submitTest(@AuthenticationPrincipal User user,
                                        @PathVariable UUID courseId,
                                        @PathVariable UUID topicId,
                                        @RequestBody @Valid TestSubmissionRequest request) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getId().equals(courseId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Topic does not belong to course"));
        }

        if (user.getRole() != Role.STUDENT) {
            return ResponseEntity.status(403).build();
        }

        TestSection test = topic.getTestSection();
        if (test == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No test section in topic"));
        }

        List<Answer> selectedAnswers = answerRepository.findAllById(request.selectedAnswers());
        int totalQuestions = test.getQuestions().size();

        // проверка: выбраны ли правильные ответы на все вопросы (и не выбраны ли неправильные)
        Map<UUID, Boolean> correctnessByQuestion = new HashMap<>();
        for (Question question : test.getQuestions()) {
            List<Answer> correct = question.getAnswers().stream()
                    .filter(Answer::getIsCorrect)
                    .toList();

            List<Answer> submitted = selectedAnswers.stream()
                    .filter(a -> a.getQuestion().getId().equals(question.getId()))
                    .toList();

            correctnessByQuestion.put(
                    question.getId(),
                    submitted.containsAll(correct) && correct.containsAll(submitted)
            );
        }

        long correctCount = correctnessByQuestion.values().stream().filter(Boolean::booleanValue).count();
        boolean passed = correctCount == totalQuestions;

        TestResult result = TestResult.builder()
                .student(user)
                .testSection(test)
                .passed(passed)
                .score((int) correctCount)
                .totalQuestions(totalQuestions)
                .build();

        testResultRepository.save(result);

        // условно обновим прогресс (по ТЗ — если все разделы темы пройдены)
        if (passed) {
            CourseProgress progress = courseProgressRepository.findByCourseAndStudent(topic.getCourse(), user)
                    .orElseThrow(() -> new RuntimeException("No enrollment found"));

            if (progress.getCompletedAt() == null) {
                progress.setCompletedAt(LocalDateTime.now());
                courseProgressRepository.save(progress);
            }
        }

        return ResponseEntity.ok(Map.of(
                "passed", passed,
                "score", correctCount,
                "totalQuestions", totalQuestions
        ));
    }
}
