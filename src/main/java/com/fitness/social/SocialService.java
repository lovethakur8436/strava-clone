package com.fitness.social;

import com.fitness.activity.Activity;
import com.fitness.activity.ActivityRepository;
import com.fitness.activity.dto.ActivityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class SocialService {

    private final FollowRepository followRepository;
    private final ActivityRepository activityRepository;

    public SocialService(FollowRepository followRepository, ActivityRepository activityRepository) {
        this.followRepository = followRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public void followUser(UUID followerId, UUID followingId) {
        // 1. Prevent users from following themselves
        if (followerId.equals(followingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot follow yourself");
        }

        // 2. Prevent duplicate follows
        followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .ifPresent(f -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already following this user");
                });

        // 3. Save the relationship
        Follow follow = Follow.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void unfollowUser(UUID followerId, UUID followingId) {
        Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not following this user"));

        followRepository.delete(follow);
    }

    public Page<ActivityResponse> getSocialFeed(UUID userId, int page, int size) {
        // 1. Find everyone I am following
        List<UUID> followingIds = followRepository.findFollowingIdsByFollowerId(userId);

        // 2. If I don't follow anyone, return an empty page immediately to save a DB
        // call
        if (followingIds.isEmpty()) {
            return Page.empty();
        }

        // 3. Fetch their activities, sorted by newest first
        int safeSize = Math.min(size, 50);
        Page<Activity> activities = activityRepository.findByUserIdInOrderByStartTimeDesc(
                followingIds,
                PageRequest.of(page, safeSize));

        return activities.map(this::mapToResponse);
    }

    // Helper to map the Entity to the safe DTO
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