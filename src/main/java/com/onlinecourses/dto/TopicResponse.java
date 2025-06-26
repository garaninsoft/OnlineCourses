package com.onlinecourses.dto;

import java.util.UUID;

public record TopicResponse(
        UUID id,
        String title,
        Integer orderIndex,
        boolean hasText,
        boolean hasVideo,
        boolean hasTest,
        boolean hasAssignment
) {}
