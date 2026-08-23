package com.fitness.profile;

import com.fitness.identity.User;
import com.fitness.identity.UserRepository;
import com.fitness.profile.dto.UserProfileResponse;
import com.fitness.social.FollowRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.cache.annotation.Cacheable;

import java.util.UUID;

@Service
public class ProfileService {

        private final UserRepository userRepository;
        private final UserStatsRepository userStatsRepository;
        private final FollowRepository followRepository;
        private final ProfileRepository profileRepository;

        public ProfileService(UserRepository userRepository,
                        ProfileRepository profileRepository, // 2. Injected it
                        UserStatsRepository userStatsRepository,
                        FollowRepository followRepository) {
                this.userRepository = userRepository;
                this.profileRepository = profileRepository;
                this.userStatsRepository = userStatsRepository;
                this.followRepository = followRepository;
        }

        @Transactional(readOnly = true)
        @Cacheable(value = "userProfile", key = "#userId")
        public UserProfileResponse getUserProfile(UUID userId) {
                // 1. Fetch Identity
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

                // 2. Fetch the actual Profile to get the names
                Profile profile = profileRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Profile missing for user"));

                // 2. Fetch Network Stats
                long followingCount = followRepository.countByFollowerId(userId);
                long followersCount = followRepository.countByFollowingId(userId);

                // 3. Fetch Lifetime Stats (Handling the Empty State safely)
                UserStats stats = userStatsRepository.findById(userId).orElse(
                                UserStats.builder()
                                                .totalActivities(0)
                                                .totalDistanceMeters(0.0)
                                                .totalDurationSeconds(0L)
                                                .build());

                // 4. Assemble the Unified Payload
                return new UserProfileResponse(
                                profile.getFirstName(), // FIXED: Pulling from Profile
                                profile.getLastName(), // FIXED: Pulling from Profile
                                stats.getTotalActivities(),
                                stats.getTotalDistanceMeters(),
                                stats.getTotalDurationSeconds(),
                                followersCount,
                                followingCount);
        }
}