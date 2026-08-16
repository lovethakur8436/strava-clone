CREATE TABLE activities (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    activity_type VARCHAR(50) NOT NULL, -- e.g., RUN, RIDE, SWIM
    start_time TIMESTAMP NOT NULL,
    distance_meters NUMERIC(10, 2) NOT NULL,
    duration_seconds INTEGER NOT NULL,
    route_data JSONB, -- Stores our massive array of GPS coordinates
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for quickly fetching a user's feed in chronological order
CREATE INDEX idx_activities_user_id_start_time ON activities(user_id, start_time DESC);