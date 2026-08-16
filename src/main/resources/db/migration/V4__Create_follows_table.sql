CREATE TABLE follows (
    id UUID PRIMARY KEY,
    follower_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    following_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- A user cannot follow the same person twice
    UNIQUE(follower_id, following_id) 
);

-- Indexes are critical here. 
-- 1. To quickly find who I am following
CREATE INDEX idx_follows_follower_id ON follows(follower_id);
-- 2. To quickly find who is following me (my audience)
CREATE INDEX idx_follows_following_id ON follows(following_id);