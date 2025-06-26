package com.onlinecourses.dto;

import java.util.List;

public record QuestionRequest(String questionText, List<AnswerRequest> answers) {}
