-- 사용자 데이터
INSERT INTO users (email, name, created_at) VALUES
('user1@test.com', '김철수', NOW()),
('user2@test.com', '이영희', NOW()),
('user3@test.com', '박민수', NOW()),
('user4@test.com', '최지영', NOW()),
('user5@test.com', '정다은', NOW()),
('user6@test.com', '홍길동', NOW()),
('user7@test.com', '김민지', NOW()),
('user8@test.com', '이준호', NOW()),
('user9@test.com', '박서연', NOW()),
('user10@test.com', '최윤석', NOW());

-- 상품 데이터
INSERT INTO products (name, price, stock, description, created_at) VALUES
('노트북', 1500000, 50, '고성능 노트북', NOW()),
('스마트폰', 800000, 100, '최신 스마트폰', NOW()),
('태블릿', 600000, 75, '휴대용 태블릿', NOW()),
('키보드', 150000, 200, '기계식 키보드', NOW()),
('마우스', 80000, 300, '게이밍 마우스', NOW()),
('모니터', 400000, 80, '4K 모니터', NOW()),
('헤드셋', 200000, 120, '무선 헤드셋', NOW()),
('웹캠', 120000, 90, 'HD 웹캠', NOW()),
('스피커', 300000, 60, '블루투스 스피커', NOW()),
('충전기', 50000, 500, '고속 충전기', NOW()),
('SSD', 200000, 150, '1TB SSD', NOW()),
('RAM', 100000, 200, '16GB DDR4', NOW()),
('그래픽카드', 800000, 30, 'RTX 4070', NOW()),
('마더보드', 300000, 40, 'ATX 마더보드', NOW()),
('파워서플라이', 150000, 80, '750W 파워', NOW());