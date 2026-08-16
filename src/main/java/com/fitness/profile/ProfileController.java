package com.fitness.profile;

import com.fitness.profile.dto.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        // Extract the secure ID from the JWT
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID userId = UUID.fromString(userIdStr);

        UserProfileResponse response = profileService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }
}