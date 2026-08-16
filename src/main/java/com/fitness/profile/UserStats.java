package com.fitness.profile;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStats {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "total_activities")
    private Integer totalActivities;

    @Column(name = "total_distance_meters", columnDefinition = "numeric")
    private Double totalDistanceMeters;

    @Column(name = "total_duration_seconds")
    private Long totalDurationSeconds;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}