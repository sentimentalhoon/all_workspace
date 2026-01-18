-- 1. Update NULL values to default '미상' (Unknown)
UPDATE bad_users SET region = '미상' WHERE region IS NULL;

-- 2. Enforce NOT NULL constraint on region
ALTER TABLE bad_users ALTER COLUMN region SET NOT NULL;

-- 3. Ensure reason is also NOT NULL (it should be from V1, but checking to be safe)
UPDATE bad_users SET reason = '사유 없음' WHERE reason IS NULL;
ALTER TABLE bad_users ALTER COLUMN reason SET NOT NULL;
