CREATE TABLE banners (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    image_url VARCHAR(512) NOT NULL,
    link_url VARCHAR(512),
    is_visible BOOLEAN DEFAULT TRUE NOT NULL,
    order_index INT DEFAULT 0 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Index for efficient querying of visible banners ordered by index
CREATE INDEX idx_banners_visible_order ON banners(is_visible, order_index);
