package com.fitness.activity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fitness.activity.dto.RoutePoint;

import java.util.ArrayList;
import java.util.List;
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
    private List<RoutePoint> routeData;

    // New field to store photo URLs from MinIO
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "photo_urls", columnDefinition = "jsonb")
    private List<String> photoUrls = new ArrayList<>();

    // New field to store map image URL from Mapbox
    @Column(name = "map_image_url")
    private String mapImageUrl;

    public String getMapImageUrl() {
        return mapImageUrl;
    }

    public void setMapImageUrl(String mapImageUrl) {
        this.mapImageUrl = mapImageUrl;
    } // public void addPhotoUrl(String url) {
    // if (this.photoUrls == null) {
    // this.photoUrls = new ArrayList<>();
    // }
    // this.photoUrls.add(url);
    // }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}