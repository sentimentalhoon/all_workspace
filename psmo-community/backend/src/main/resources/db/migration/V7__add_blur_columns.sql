-- Add blur_url and blur_thumbnail_url to bad_user_images
ALTER TABLE bad_user_images ADD COLUMN IF NOT EXISTS blur_url VARCHAR(500);
ALTER TABLE bad_user_images ADD COLUMN IF NOT EXISTS blur_thumbnail_url VARCHAR(500);

-- Add blur_url and blur_thumbnail_url to product_images
ALTER TABLE product_images ADD COLUMN IF NOT EXISTS blur_url VARCHAR(512);
ALTER TABLE product_images ADD COLUMN IF NOT EXISTS blur_thumbnail_url VARCHAR(512);
