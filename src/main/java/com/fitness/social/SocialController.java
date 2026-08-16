package com.fitness.social;

import com.fitness.activity.dto.ActivityResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/social")
public class SocialController {

    private final SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    @PostMapping("/users/{targetUserId}/follow")
    public ResponseEntity<Void> followUser(@PathVariable UUID targetUserId) {
        UUID currentUserId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        socialService.followUser(currentUserId, targetUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/users/{targetUserId}/follow")
    public ResponseEntity<Void> unfollowUser(@PathVariable UUID targetUserId) {
        UUID currentUserId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        socialService.unfollowUser(currentUserId, targetUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<ActivityResponse>> getMyFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UUID currentUserId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        Page<ActivityResponse> feed = socialService.getSocialFeed(currentUserId, page, size);
        return ResponseEntity.ok(feed);
    }
}