-- Add role, score, activity level to users
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    ADD COLUMN IF NOT EXISTS score INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS activity_level INTEGER NOT NULL DEFAULT 1;

-- Backfill existing rows with defaults (idempotent)
UPDATE users
SET role = COALESCE(role, 'MEMBER'),
    score = COALESCE(score, 0),
    activity_level = COALESCE(activity_level, 1);

COMMENT ON COLUMN users.role IS '시스템 역할: SYSTEM/ADMIN/MANAGER/MODERATOR/MEMBER';
COMMENT ON COLUMN users.score IS '활동 점수 (게시/댓글 등 누적)';
COMMENT ON COLUMN users.activity_level IS '활동 레벨 (점수 구간 기반)';
