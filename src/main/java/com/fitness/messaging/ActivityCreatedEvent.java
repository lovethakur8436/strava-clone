package com.fitness.messaging;

import java.util.UUID;

// A lightweight JSON payload for the Message Broker
public record ActivityCreatedEvent(
        UUID activityId,
        UUID userId,
        Double distanceMeters) {
}