package com.onlinecourses.controller;

import com.onlinecourses.dto.TestSectionRequest;
import com.onlinecourses.dto.QuestionRequest;
import com.onlinecourses.dto.AnswerRequest;
import com.onlinecourses.entity.*;
import com.onlinecourses.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/topics/{topicId}/test")
@RequiredArgsConstructor
public class TestSectionController {

    private final TopicRepository topicRepository;
    private final TestSectionRepository testSectionRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @PostMapping
    public ResponseEntity<?> createTestSection(@AuthenticationPrincipal User user,
                                               @PathVariable UUID topicId,
                                               @RequestBody @Valid TestSectionRequest request) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        TestSection testSection = new TestSection();
        testSection.setOrderIndex(request.orderIndex());
        testSection.setTopic(topic);
        testSectionRepository.save(testSection);

        for (QuestionRequest q : request.questions()) {
            Question question = new Question();
            question.setQuestionText(q.questionText());
            question.setTestSection(testSection);
            questionRepository.save(question);

            for (AnswerRequest a : q.answers()) {
                Answer answer = new Answer();
                answer.setAnswerText(a.answerText());
                answer.setIsCorrect(a.isCorrect());
                answer.setQuestion(question);
                answerRepository.save(answer);
            }
        }

        topic.setTestSection(testSection);
        topicRepository.save(topic);

        return ResponseEntity.ok(Map.of("message", "Test section with questions and answers created"));
    }

    @GetMapping
    public ResponseEntity<?> getTest(@PathVariable UUID topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        TestSection test = topic.getTestSection();
        if (test == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(Map.of(
                "id", test.getId(),
                "orderIndex", test.getOrderIndex(),
                "questionsCount", test.getQuestions().size()
        ));
    }
}
