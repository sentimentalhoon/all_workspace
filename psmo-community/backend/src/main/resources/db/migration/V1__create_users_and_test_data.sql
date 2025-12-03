COMMENT ON SCHEMA public IS 'PSMO 커뮤니티 기본 스키마';

-- Telegram 로그인 사용자를 저장하는 메인 테이블
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    telegram_id BIGINT NOT NULL UNIQUE,
    display_name VARCHAR(150),
    username VARCHAR(150),
    photo_url VARCHAR(512),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE users IS 'Telegram 로그인 기반 회원 정보';
COMMENT ON COLUMN users.telegram_id IS 'Telegram user ID (unique)';
COMMENT ON COLUMN users.display_name IS '이름/성 조합 표시 이름';
COMMENT ON COLUMN users.username IS 'Telegram username (nullable)';
COMMENT ON COLUMN users.photo_url IS 'Telegram 프로필 이미지 URL';
COMMENT ON COLUMN users.created_at IS '레코드 생성 시각';
COMMENT ON COLUMN users.updated_at IS '마지막 갱신 시각';

-- 인프라 연결 점검을 위한 샘플 데이터 테이블
CREATE TABLE IF NOT EXISTS test_data (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE test_data IS '테스트/헬스체크용 데이터';
COMMENT ON COLUMN test_data.name IS '테스트 항목 이름';
COMMENT ON COLUMN test_data.value IS '테스트 값';
COMMENT ON COLUMN test_data.created_at IS '테스트 데이터 생성 시각';
