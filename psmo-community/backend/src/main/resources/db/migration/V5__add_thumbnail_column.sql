-- Add thumbnail_url column to bad_user_images
ALTER TABLE bad_user_images ADD COLUMN IF NOT EXISTS thumbnail_url VARCHAR(500);

-- Populate existing rows: use 'url' as fallback for 'thumbnail_url'
UPDATE bad_user_images SET thumbnail_url = url WHERE thumbnail_url IS NULL;

-- Enforce NOT NULL
ALTER TABLE bad_user_images ALTER COLUMN thumbnail_url SET NOT NULL;
