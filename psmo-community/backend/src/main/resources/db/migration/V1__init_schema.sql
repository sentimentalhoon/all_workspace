-- PSMO Community Integrated Schema (Consolidated V1)

-- 1. Users & Auth
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    telegram_id BIGINT NOT NULL UNIQUE,
    display_name VARCHAR(150),
    username VARCHAR(150),
    photo_url VARCHAR(512),
    -- From V4
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    score INTEGER NOT NULL DEFAULT 0,
    activity_level INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE users IS 'Telegram Login Users';
COMMENT ON COLUMN users.role IS 'SYSTEM/ADMIN/MANAGER/MODERATOR/MEMBER';

-- 2. Bad Users (Blacklist)
CREATE TABLE bad_users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone_hash VARCHAR(64) NOT NULL,
    phone_last4 VARCHAR(4) NOT NULL,
    birth_year INT,
    reason VARCHAR(500) NOT NULL,
    reporter_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bad_users_reporter FOREIGN KEY (reporter_id) REFERENCES users(id)
);

CREATE TABLE bad_user_images (
    id BIGSERIAL PRIMARY KEY,
    bad_user_id BIGINT NOT NULL,
    url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bad_user_images_bad_user FOREIGN KEY (bad_user_id) REFERENCES bad_users(id)
);

CREATE INDEX idx_bad_users_phone_last4 ON bad_users(phone_last4);
CREATE INDEX idx_bad_users_name ON bad_users(name);

-- 3. Products (Market)
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'SALE', 
    category VARCHAR(50) NOT NULL, 
    seller_id BIGINT NOT NULL REFERENCES users(id),
    view_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_seller_id ON products(seller_id);

CREATE TABLE product_real_estate_info (
    product_id BIGINT PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
    location_city VARCHAR(50) NOT NULL,
    location_district VARCHAR(50) NOT NULL,
    pc_count INT NOT NULL DEFAULT 0,
    deposit BIGINT NOT NULL DEFAULT 0,
    monthly_rent INT NOT NULL DEFAULT 0,
    management_fee INT NOT NULL DEFAULT 0,
    average_monthly_revenue BIGINT NOT NULL DEFAULT 0,
    floor INT,
    area_meters FLOAT
);

CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    url VARCHAR(512) NOT NULL,
    type VARCHAR(20) NOT NULL, -- 'IMAGE' or 'VIDEO'
    order_index INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_images_product_id ON product_images(product_id);

-- 4. Community Board
-- Changed AUTO_INCREMENT to BIGSERIAL for consistency
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    category VARCHAR(20) NOT NULL,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE post_images (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    url VARCHAR(255) NOT NULL,
    order_index INT DEFAULT 0,
    FOREIGN KEY (post_id) REFERENCES posts(id)
);

CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE post_likes (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE (post_id, user_id)
);

-- 5. Test Data
CREATE TABLE IF NOT EXISTS test_data (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
