package com.fitness.social;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {

    // Check if a relationship already exists (to prevent duplicate follows or allow
    // unfollowing)
    Optional<Follow> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    long countByFollowerId(UUID followerId);

    long countByFollowingId(UUID followingId);

    // Get a flat list of UUIDs of all the users I am following.
    // We will inject this list into our Activity query to build the feed.
    @Query("SELECT f.followingId FROM Follow f WHERE f.followerId = :followerId")
    List<UUID> findFollowingIdsByFollowerId(@Param("followerId") UUID followerId);
}