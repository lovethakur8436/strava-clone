package com.fitness.profile.dto;

public record UserProfileResponse(
        String firstName,
        String lastName,
        Integer totalActivities,
        Double totalDistanceMeters,
        Long totalDurationSeconds,
        Long followersCount,
        Long followingCount) {
}