package com.fitness.activity;

import com.fitness.activity.dto.ActivityResponse;
import com.fitness.activity.dto.ActivityUpdateRequest;
import com.fitness.activity.dto.ActivityUploadRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ActivityResponse> updateActivity(
            @PathVariable UUID id,
            @RequestBody ActivityUpdateRequest request) {
        return ResponseEntity.ok(activityService.updateActivity(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable UUID id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ActivityResponse> uploadActivity(
            @Valid @RequestBody ActivityUploadRequest request) {

        // 1. Extract the secure User ID from the ThreadLocal VIP wristband
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID userId = UUID.fromString(userIdStr);

        // 2. Process and save
        ActivityResponse response = activityService.saveActivity(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActivityResponse> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        ActivityResponse response = activityService.uploadActivityPhoto(id, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ActivityResponse>> getMyActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID userId = UUID.fromString(userIdStr);

        Page<ActivityResponse> feed = activityService.getUserActivities(userId, page, size);

        return ResponseEntity.ok(feed);
    }
}