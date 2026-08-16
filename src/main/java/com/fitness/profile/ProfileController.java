package com.fitness.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    @GetMapping("/me")
    public ResponseEntity<String> getMyProfile() {
        // 1. Ask Spring Security: "Who is making this request?"
        // This extracts the User ID that our JwtAuthenticationFilter put into
        // ThreadLocal memory.
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Return a simple dummy response for now.
        // In Phase 2, we will use this userId to query the database.
        return ResponseEntity.ok("Successfully reached protected endpoint! Your User ID is: " + userId);
    }
}