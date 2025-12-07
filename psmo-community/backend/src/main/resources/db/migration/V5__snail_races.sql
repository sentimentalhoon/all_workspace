CREATE TABLE IF NOT EXISTS snail_races (
    race_id VARCHAR(80) PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    snail_id INT NOT NULL,
    bet_amount INT NOT NULL,
    server_seed VARCHAR(128) NOT NULL,
    client_seed VARCHAR(128),
    combined_seed VARCHAR(256) NOT NULL,
    track_length INT NOT NULL,
    snails_json TEXT NOT NULL,
    fairness_hash VARCHAR(128) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL,
    reported BOOLEAN NOT NULL DEFAULT FALSE,
    verified BOOLEAN,
    payout INT,
    winner_ids TEXT,
    frames INT,
    duration_ms BIGINT
);

CREATE INDEX IF NOT EXISTS idx_snail_races_user_id ON snail_races(user_id);
CREATE INDEX IF NOT EXISTS idx_snail_races_expires_at ON snail_races(expires_at);

CREATE TABLE IF NOT EXISTS snail_race_logs (
    id BIGSERIAL PRIMARY KEY,
    race_id VARCHAR(80) NOT NULL REFERENCES snail_races(race_id) ON DELETE CASCADE,
    client_log TEXT,
    server_state TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_snail_race_logs_race_id ON snail_race_logs(race_id);
