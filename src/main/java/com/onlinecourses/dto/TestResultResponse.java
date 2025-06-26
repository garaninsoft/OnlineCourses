package com.onlinecourses.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TestResultResponse(
        UUID id,
        Boolean passed,
        Integer score,
        Integer totalQuestions,
        LocalDateTime completedAt
) {}
