package com.fitness.activity.dto;

import com.fitness.activity.RoutePoint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ActivityResponse(
        UUID id,
        String title,
        String activityType,
        LocalDateTime startTime,
        Double distanceMeters,
        Integer durationSeconds,
        List<RoutePoint> routeData // Updated here too
) {
}