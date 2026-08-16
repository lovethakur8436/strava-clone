package com.fitness.activity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String title;

    @Column(name = "activity_type", nullable = false)
    private String activityType;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "distance_meters", nullable = false, columnDefinition = "numeric")
    private Double distanceMeters;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    // Tells Hibernate to treat this String specifically as a PostgreSQL JSONB
    // document
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "route_data", columnDefinition = "jsonb")
    private String routeData;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}