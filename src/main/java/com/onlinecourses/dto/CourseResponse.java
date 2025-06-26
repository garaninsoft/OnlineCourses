package com.onlinecourses.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CourseResponse(
        UUID id,
        String title,
        String description,
        String creatorName,
        LocalDateTime createdAt,
        int topicsCount
) {}
