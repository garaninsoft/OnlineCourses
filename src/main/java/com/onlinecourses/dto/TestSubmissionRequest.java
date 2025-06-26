package com.onlinecourses.dto;

import java.util.List;
import java.util.UUID;

public record TestSubmissionRequest(List<UUID> selectedAnswers) {}

