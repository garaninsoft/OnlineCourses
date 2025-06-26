package com.onlinecourses.controller;

import com.onlinecourses.dto.TextSectionRequest;
import com.onlinecourses.entity.TextSection;
import com.onlinecourses.entity.Topic;
import com.onlinecourses.entity.User;
import com.onlinecourses.repository.TextSectionRepository;
import com.onlinecourses.repository.TopicRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/topics/{topicId}/text")
@RequiredArgsConstructor
public class TextSectionController {

    private final TopicRepository topicRepository;
    private final TextSectionRepository textSectionRepository;

    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal User user,
                                    @PathVariable UUID topicId,
                                    @RequestBody @Valid TextSectionRequest request) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        TextSection section = new TextSection();
        section.setContent(request.content());
        section.setTopic(topic);

        textSectionRepository.save(section);
        topic.setTextSection(section);
        topicRepository.save(topic);

        return ResponseEntity.ok(Map.of("message", "Text section created"));
    }

    @PutMapping
    public ResponseEntity<?> update(@AuthenticationPrincipal User user,
                                    @PathVariable UUID topicId,
                                    @RequestBody @Valid TextSectionRequest request) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        TextSection section = topic.getTextSection();
        section.setContent(request.content());
        textSectionRepository.save(section);

        return ResponseEntity.ok(Map.of("message", "Text section updated"));
    }

    @GetMapping
    public ResponseEntity<?> get(@PathVariable UUID topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        TextSection section = topic.getTextSection();
        return ResponseEntity.ok(Map.of(
                "id", section.getId(),
                "content", section.getContent()
        ));
    }
}
