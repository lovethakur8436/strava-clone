-- Add a JSONB column to hold an array of image URLs
ALTER TABLE activities 
ADD COLUMN photo_urls JSONB DEFAULT '[]'::jsonb;