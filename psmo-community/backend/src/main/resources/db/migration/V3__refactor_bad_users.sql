-- 블랙리스트 테이블 리팩토링: 개인정보 필드 제거, 새 필드 추가

-- 새 컬럼 추가
ALTER TABLE bad_users ADD COLUMN IF NOT EXISTS region VARCHAR(100);
ALTER TABLE bad_users ADD COLUMN IF NOT EXISTS physical_description VARCHAR(500);
ALTER TABLE bad_users ADD COLUMN IF NOT EXISTS incident_date DATE;

-- reason 컬럼 길이 확장 (500 -> 2000)
ALTER TABLE bad_users ALTER COLUMN reason TYPE VARCHAR(2000);

-- 기존 개인정보 컬럼 삭제
ALTER TABLE bad_users DROP COLUMN IF EXISTS name;
ALTER TABLE bad_users DROP COLUMN IF EXISTS phone_hash;
ALTER TABLE bad_users DROP COLUMN IF EXISTS phone_last4;
ALTER TABLE bad_users DROP COLUMN IF EXISTS birth_year;
