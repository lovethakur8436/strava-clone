package com.fitness.activity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    // Spring Data JPA automatically converts this to:
    // SELECT * FROM activities WHERE user_id = ? ORDER BY start_time DESC LIMIT ?
    // OFFSET ?
    Page<Activity> findByUserIdOrderByStartTimeDesc(UUID userId, Pageable pageable);

    // Spring translates 'UserIdIn' to a SQL 'WHERE user_id IN (?, ?, ?)' clause
    Page<Activity> findByUserIdInOrderByStartTimeDesc(List<UUID> userIds, Pageable pageable);
}