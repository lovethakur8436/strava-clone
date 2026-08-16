package com.fitness.activity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityResponse(
        UUID id,
        String title,
        String activityType,
        LocalDateTime startTime,
        Double distanceMeters,
        Integer durationSeconds,
        String routeData) {
}