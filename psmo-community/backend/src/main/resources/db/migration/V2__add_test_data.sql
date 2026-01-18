-- V2__add_test_data.sql
-- 테스트 데이터 생성 (Bad Users 50개, Products 50개)

-- 1. FK 만족을 위한 더미 사용자 (System Admin / Tester)
INSERT INTO users (telegram_id, display_name, username, photo_url, role, score, activity_level)
VALUES (999999999, 'Test Admin', 'test_admin', NULL, 'ADMIN', 1000, 5)
ON CONFLICT (telegram_id) DO NOTHING;

-- 테스트용 변수 설정 (Postgres/H2 호환성을 위해 서브쿼리 활용)
-- reporter_id / seller_id 로 사용할 ID 조회
-- (실제 실행 시 위에서 방금 넣은 user의 ID를 가져옴)

-- 2. Bad Users (50개)
INSERT INTO bad_users (name, phone_hash, phone_last4, birth_year, reason, reporter_id, created_at)
SELECT 
    'BadUser_' || x, 
    'HASH_' || x, 
    LPAD(CAST(FLOOR(RANDOM() * 10000) AS VARCHAR), 4, '0'), 
    1980 + CAST(FLOOR(RANDOM() * 20) AS INT),
    CASE CAST(FLOOR(RANDOM() * 5) AS INT)
        WHEN 0 THEN '요금 미지불 도주'
        WHEN 1 THEN '기물 파손 후 잠적'
        WHEN 2 THEN '직원 폭행 및 난동'
        WHEN 3 THEN '타 손님에게 시비'
        ELSE '상습적인 고성방가'
    END,
    (SELECT id FROM users WHERE telegram_id = 999999999),
    CURRENT_TIMESTAMP - (CAST(FLOOR(RANDOM() * 30) AS INT) || ' DAYS')::INTERVAL
FROM GENERATE_SERIES(1, 50) AS x;

-- 3. Products (50개)
-- 먼저 product 테이블에 insert 하고, 리턴되는 ID를 굳이 잡기 힘드므로
-- 여기서는 단순히 루프 돌면서 insert 하는 방식보다는,
-- H2/Postgres 호환성을 위해 INSERT INTO ... SELECT 방식을 사용하되,
-- Real Estate Info 와의 연결을 위해 시퀀스를 활용하거나 단순 데이터를 넣습니다.

-- Products 50개 생성
INSERT INTO products (title, description, price, status, category, seller_id, view_count, created_at)
SELECT 
    CASE CAST(FLOOR(RANDOM() * 4) AS INT)
        WHEN 0 THEN '서울 번화가 PC방 매매합니다'
        WHEN 1 THEN 'RTX 4060 50대 일괄 처분'
        WHEN 2 THEN '인테리어 최고급 급매'
        ELSE '무인 관제 시스템 양도'
    END || ' #' || x,
    '성업 중인 매장입니다. 개인 사정으로 급매합니다. 매출 인증 가능. 상세 내용은 연락 주세요.',
    10000000 + CAST(FLOOR(RANDOM() * 10000000) AS INT),
    CASE CAST(FLOOR(RANDOM() * 3) AS INT)
        WHEN 0 THEN 'SALE'
        WHEN 1 THEN 'RESERVED'
        ELSE 'SOLD'
    END,
    CASE CAST(FLOOR(RANDOM() * 5) AS INT)
        WHEN 0 THEN 'PC_FULL'
        WHEN 1 THEN 'PC_BUSINESS'
        WHEN 2 THEN 'GPU'
        WHEN 3 THEN 'MONITOR'
        ELSE 'ETC'
    END,
    (SELECT id FROM users WHERE telegram_id = 999999999),
    CAST(FLOOR(RANDOM() * 500) AS INT),
    CURRENT_TIMESTAMP - (CAST(FLOOR(RANDOM() * 60) AS INT) || ' DAYS')::INTERVAL
FROM GENERATE_SERIES(1, 50) AS x;

-- Product Real Estate Info (위에서 생성된 Products와 1:1 매핑)
-- 주의: 위 INSERT가 실행된 후의 ID 범위를 정확히 알기 어려울 수 있으나,
-- 테스트 DB 특성상 방금 넣은 50개가 가장 최근 것임을 가정하고 넣습니다.
-- 혹은, 단순함을 위해 서브쿼리로 연결합니다.

INSERT INTO product_real_estate_info (product_id, location_city, location_district, pc_count, deposit, monthly_rent, management_fee, average_monthly_revenue, floor, area_meters)
SELECT 
    id,
    '서울',
    CASE CAST(FLOOR(RANDOM() * 5) AS INT)
        WHEN 0 THEN '강남구'
        WHEN 1 THEN '서초구'
        WHEN 2 THEN '마포구'
        WHEN 3 THEN '송파구'
        ELSE '영등포구'
    END,
    50 + CAST(FLOOR(RANDOM() * 100) AS INT),
    50000000,
    3000000,
    500000,
    15000000,
    2,
    150.5
FROM products
WHERE seller_id = (SELECT id FROM users WHERE telegram_id = 999999999)
AND NOT EXISTS (SELECT 1 FROM product_real_estate_info WHERE product_id = products.id);
