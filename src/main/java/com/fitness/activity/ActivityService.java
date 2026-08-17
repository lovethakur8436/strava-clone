package com.fitness.activity;

import com.fitness.activity.dto.ActivityResponse;
import com.fitness.activity.dto.ActivityUploadRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fitness.profile.UserStatsRepository;
import com.fitness.social.LeaderboardService;

import java.util.UUID;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserStatsRepository userStatsRepository;
    private final LeaderboardService leaderboardService;

    public ActivityService(ActivityRepository activityRepository, UserStatsRepository userStatsRepository,
            LeaderboardService leaderboardService) {
        this.activityRepository = activityRepository;
        this.userStatsRepository = userStatsRepository;
        this.leaderboardService = leaderboardService;
    }

    @Transactional
    public ActivityResponse saveActivity(UUID userId, ActivityUploadRequest request) {

        Activity activity = Activity.builder()
                .userId(userId) // Stamped directly from the JWT, completely secure
                .title(request.title())
                .activityType(request.activityType())
                .startTime(request.startTime())
                .distanceMeters(request.distanceMeters())
                .durationSeconds(request.durationSeconds())
                .routeData(request.routeData()) // The massive JSONB payload
                .build();

        Activity savedActivity = activityRepository.save(activity);

        userStatsRepository.incrementStatsAtomically(
                userId,
                request.distanceMeters(),
                request.durationSeconds());

        leaderboardService.incrementUserDistance(userId, request.distanceMeters());

        return mapToResponse(savedActivity);
    }

    public Page<ActivityResponse> getUserActivities(UUID userId, int page, int size) {
        // Enforce maximum page size so a malicious client can't request 1,000,000 items
        int safeSize = Math.min(size, 50);

        Page<Activity> activityPage = activityRepository.findByUserIdOrderByStartTimeDesc(
                userId,
                PageRequest.of(page, safeSize));

        // Convert the Page of Entities into a Page of DTOs
        return activityPage.map(this::mapToResponse);
    }

    // Helper method to enforce the DTO firewall
    private ActivityResponse mapToResponse(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getTitle(),
                activity.getActivityType(),
                activity.getStartTime(),
                activity.getDistanceMeters(),
                activity.getDurationSeconds(),
                activity.getRouteData());
    }
}