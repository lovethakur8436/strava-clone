package com.fitness.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    // Explicitly find the profile using the foreign key
    Optional<Profile> findByUserId(UUID userId);
}