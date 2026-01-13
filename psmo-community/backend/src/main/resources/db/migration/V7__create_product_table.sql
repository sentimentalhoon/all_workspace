CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'SALE', -- SALE, RESERVED, SOLD
    category VARCHAR(50) NOT NULL, -- PC_FULL, PART, MONITOR, ETC
    seller_id BIGINT NOT NULL REFERENCES users(id),
    view_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_seller_id ON products(seller_id);
