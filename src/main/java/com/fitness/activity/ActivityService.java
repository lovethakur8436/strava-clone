package com.fitness.activity;

import com.fitness.activity.dto.ActivityResponse;
import com.fitness.activity.dto.ActivityUpdateRequest;
import com.fitness.activity.dto.ActivityUploadRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fitness.profile.UserStatsRepository;
import com.fitness.social.LeaderboardService;
import org.springframework.cache.annotation.CacheEvict;

import com.fitness.messaging.ActivityCreatedEvent;
import com.fitness.messaging.ActivityEventPublisher;

import com.fitness.storage.ImageStorageService;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class ActivityService {

        private final ActivityRepository activityRepository;
        private final UserStatsRepository userStatsRepository;
        private final LeaderboardService leaderboardService;
        private final ActivityEventPublisher eventPublisher;

        private final ImageStorageService imageStorageService;

        public ActivityService(ActivityRepository activityRepository, UserStatsRepository userStatsRepository,
                        LeaderboardService leaderboardService, ActivityEventPublisher eventPublisher,
                        ImageStorageService imageStorageService) {
                this.activityRepository = activityRepository;
                this.userStatsRepository = userStatsRepository;
                this.leaderboardService = leaderboardService;
                this.eventPublisher = eventPublisher;
                this.imageStorageService = imageStorageService;
        }

        @Transactional
        @CacheEvict(value = "userProfile", key = "#userId")
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

                ActivityCreatedEvent event = new ActivityCreatedEvent(
                                savedActivity.getId(),
                                userId,
                                request.distanceMeters());

                eventPublisher.publishActivityCreated(event);

                return mapToResponse(savedActivity);
        }

        @Transactional
        public ActivityResponse uploadActivityPhoto(UUID activityId, MultipartFile file) {
                // 1. Find the activity
                Activity activity = activityRepository.findById(activityId)
                                .orElseThrow(() -> new RuntimeException("Activity not found"));

                // 2. Upload the raw bytes to MinIO/S3
                String s3Url = imageStorageService.uploadImage(file);

                // 3. Add the URL to the JSONB array
                if (activity.getPhotoUrls() == null) {
                        activity.setPhotoUrls(new ArrayList<>());
                }
                activity.getPhotoUrls().add(s3Url);

                // 4. Save and return
                Activity savedActivity = activityRepository.save(activity);
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

        @Transactional
        public ActivityResponse updateActivity(UUID id, ActivityUpdateRequest request) {
                Activity activity = activityRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Activity not found"));

                if (request.title() != null && !request.title().isBlank()) {
                        activity.setTitle(request.title());
                }
                if (request.activityType() != null && !request.activityType().isBlank()) {
                        activity.setActivityType(request.activityType());
                }

                Activity savedActivity = activityRepository.save(activity);
                return mapToResponse(savedActivity);
        }

        @Transactional
        public void deleteActivity(UUID id) {
                // TODO for SDE-3: Publish an event to RabbitMQ to delete S3 images
                activityRepository.deleteById(id);
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
                                activity.getRouteData(),
                                activity.getPhotoUrls(),
                                activity.getMapImageUrl());
        }
}