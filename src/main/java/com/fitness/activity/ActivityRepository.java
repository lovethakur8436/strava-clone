package com.fitness.activity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    // Spring Data JPA automatically converts this to:
    // SELECT * FROM activities WHERE user_id = ? ORDER BY start_time DESC LIMIT ?
    // OFFSET ?
    Page<Activity> findByUserIdOrderByStartTimeDesc(UUID userId, Pageable pageable);
}