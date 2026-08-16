package com.fitness.activity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ActivityUploadRequest(
        @NotBlank(message = "Title cannot be empty") String title,

        @NotBlank(message = "Activity type is required") String activityType,

        @NotNull(message = "Start time is required") LocalDateTime startTime,

        @Min(value = 0, message = "Distance cannot be negative") Double distanceMeters,

        @Min(value = 1, message = "Duration must be at least 1 second") Integer durationSeconds,

        // We accept the massive GPS array as raw JSON text from the frontend
        String routeData) {
}