package com.onlinecourses.controller;

import com.onlinecourses.dto.VideoSectionRequest;
import com.onlinecourses.entity.Topic;
import com.onlinecourses.entity.User;
import com.onlinecourses.entity.VideoSection;
import com.onlinecourses.repository.TopicRepository;
import com.onlinecourses.repository.VideoSectionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/topics/{topicId}/video")
@RequiredArgsConstructor
public class VideoSectionController {

    private final TopicRepository topicRepository;
    private final VideoSectionRepository videoSectionRepository;

    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal User user,
                                    @PathVariable UUID topicId,
                                    @RequestBody @Valid VideoSectionRequest request) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        VideoSection section = new VideoSection();
        section.setVideoUrl(request.videoUrl());
        section.setTopic(topic);

        videoSectionRepository.save(section);
        topic.setVideoSection(section);
        topicRepository.save(topic);

        return ResponseEntity.ok(Map.of("message", "Video section created"));
    }

    @PutMapping
    public ResponseEntity<?> update(@AuthenticationPrincipal User user,
                                    @PathVariable UUID topicId,
                                    @RequestBody @Valid VideoSectionRequest request) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        VideoSection section = topic.getVideoSection();
        section.setVideoUrl(request.videoUrl());
        videoSectionRepository.save(section);

        return ResponseEntity.ok(Map.of("message", "Video section updated"));
    }

    @GetMapping
    public ResponseEntity<?> get(@PathVariable UUID topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        VideoSection section = topic.getVideoSection();
        return ResponseEntity.ok(Map.of(
                "id", section.getId(),
                "videoUrl", section.getVideoUrl()
        ));
    }
}
