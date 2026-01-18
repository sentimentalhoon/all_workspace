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
