package com.onlinecourses.dto;

import java.util.UUID;

public record CreatorRequestDto(UUID userId, String userName, String userEmail) {}
