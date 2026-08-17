package com.fitness.social;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    private static final String LEADERBOARD_KEY = "leaderboard:global:distance";
    private final RedisTemplate<String, String> redisTemplate;

    public LeaderboardService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 1. Update the score (Called every time a user uploads an activity)
    public void incrementUserDistance(UUID userId, Double distanceMeters) {
        redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, userId.toString(), distanceMeters);
    }

    // 2. Fetch the Top 10
    public Set<String> getTop10Users() {
        // reverseRange gets the highest scores first (descending order)
        Set<ZSetOperations.TypedTuple<String>> top10 = redisTemplate.opsForZSet()
                .reverseRangeWithScores(LEADERBOARD_KEY, 0, 9);

        if (top10 == null)
            return Set.of();

        // Convert the Redis tuples into a clean set of User IDs
        return top10.stream()
                .map(tuple -> "User: " + tuple.getValue() + " - Distance: " + tuple.getScore())
                .collect(Collectors.toSet());
    }

    // 3. Find a specific user's exact rank
    public Long getUserRank(UUID userId) {
        // reverseRank means #0 is the highest score
        Long rank = redisTemplate.opsForZSet().reverseRank(LEADERBOARD_KEY, userId.toString());

        // Return rank + 1 so the top user is #1, not #0
        return rank != null ? rank + 1 : null;
    }
}