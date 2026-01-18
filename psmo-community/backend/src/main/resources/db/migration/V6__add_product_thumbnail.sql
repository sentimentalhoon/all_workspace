-- Add thumbnail_url column to product_images
ALTER TABLE product_images ADD COLUMN IF NOT EXISTS thumbnail_url VARCHAR(512);

-- Populate existing rows: use 'url' as fallback for 'thumbnail_url'
UPDATE product_images SET thumbnail_url = url WHERE thumbnail_url IS NULL;

-- Enforce NOT NULL
ALTER TABLE product_images ALTER COLUMN thumbnail_url SET NOT NULL;
