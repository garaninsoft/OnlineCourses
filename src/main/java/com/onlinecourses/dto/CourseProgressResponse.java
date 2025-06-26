package com.onlinecourses.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CourseProgressResponse(
        UUID id,
        UUID courseId,
        String courseTitle,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        boolean isCompleted
) {}
