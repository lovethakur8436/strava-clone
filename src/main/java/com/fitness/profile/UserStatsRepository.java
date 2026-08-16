package com.fitness.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserStatsRepository extends JpaRepository<UserStats, UUID> {

    // @Modifying tells Spring this isn't a SELECT query, it's modifying data
    @Modifying
    @Query(value = """
            INSERT INTO user_stats (user_id, total_activities, total_distance_meters, total_duration_seconds)
            VALUES (:userId, 1, :distance, :duration)
            ON CONFLICT (user_id)
            DO UPDATE SET
                total_activities = user_stats.total_activities + 1,
                total_distance_meters = user_stats.total_distance_meters + :distance,
                total_duration_seconds = user_stats.total_duration_seconds + :duration,
                updated_at = CURRENT_TIMESTAMP
            """, nativeQuery = true)
    void incrementStatsAtomically(@Param("userId") UUID userId,
            @Param("distance") Double distance,
            @Param("duration") Integer duration);
}