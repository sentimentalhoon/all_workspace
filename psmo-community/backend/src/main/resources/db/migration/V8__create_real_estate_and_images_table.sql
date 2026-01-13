-- Add PC_BUSINESS to generic product category is not strictly possible with ENUM in SQL if hardcoded.
-- But we can just use the string value in the application and ensure the column is big enough.
-- 'product_real_estate_info' table for detailed business info
CREATE TABLE product_real_estate_info (
    product_id BIGINT PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
    location_city VARCHAR(50) NOT NULL,
    location_district VARCHAR(50) NOT NULL,
    pc_count INT NOT NULL DEFAULT 0,
    deposit BIGINT NOT NULL DEFAULT 0, -- 보증금
    monthly_rent INT NOT NULL DEFAULT 0, -- 월세
    management_fee INT NOT NULL DEFAULT 0, -- 관리비
    average_monthly_revenue BIGINT NOT NULL DEFAULT 0, -- 월 매출
    floor INT,
    area_meters FLOAT
);

-- 'product_images' table for media
CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    url VARCHAR(512) NOT NULL,
    type VARCHAR(20) NOT NULL, -- 'IMAGE' or 'VIDEO'
    order_index INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_images_product_id ON product_images(product_id);
