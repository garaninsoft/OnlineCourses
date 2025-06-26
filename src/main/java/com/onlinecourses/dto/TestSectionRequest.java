package com.onlinecourses.dto;

import java.util.List;

public record TestSectionRequest(List<QuestionRequest> questions, Integer orderIndex) {}
