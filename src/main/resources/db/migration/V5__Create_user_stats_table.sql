CREATE TABLE user_stats (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    total_activities INTEGER DEFAULT 0,
    total_distance_meters NUMERIC(12, 2) DEFAULT 0,
    total_duration_seconds BIGINT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);