package com.fitness.activity.dto;

import jakarta.validation.constraints.NotNull;

public record RoutePoint(
                @NotNull Double lat,
                @NotNull Double lng,
                @NotNull Integer time // Seconds since the activity started
) {
}