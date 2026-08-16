package com.fitness.profile.dto;

import java.util.UUID;

public record ProfileDto(
        UUID userId,
        String firstName,
        String lastName,
        String bio,
        Double weightKg) {
}