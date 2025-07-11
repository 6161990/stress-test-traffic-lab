-- 테이블 생성 DDL
CREATE TABLE IF NOT EXISTS users
(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    price       DECIMAL(10, 2) NOT NULL,
    stock       INTEGER        NOT NULL,
    description VARCHAR(1000),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT         NOT NULL REFERENCES users (id),
    total_amount DECIMAL(10, 2) NOT NULL,
    status       VARCHAR(50) DEFAULT 'PENDING',
    created_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items
(
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT         NOT NULL REFERENCES orders (id),
    product_id BIGINT         NOT NULL REFERENCES products (id),
    quantity   INTEGER        NOT NULL,
    price      DECIMAL(10, 2) NOT NULL
);

-- 사용자 데이터
INSERT INTO users (id, email, name, created_at)
VALUES (1,'user1@test.com', '김철수', NOW()),
       (2,'user2@test.com', '이영희', NOW()),
       (3,'user3@test.com', '박민수', NOW()),
       (4,'user4@test.com', '최지영', NOW()),
       (5,'user5@test.com', '정다은', NOW()),
       (6,'user6@test.com', '홍길동', NOW()),
       (7,'user7@test.com', '김민지', NOW()),
       (8,'user8@test.com', '이준호', NOW()),
       (9,'user9@test.com', '박서연', NOW()),
       (10,'user10@test.com', '최윤석', NOW()),
       (11,'user11@test.com', '차차차', NOW());

-- 상품 데이터
INSERT INTO products (name, price, stock, description, created_at)
VALUES ('노트북', 1500000, 50, '고성능 노트북', NOW()),
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


-- 주문 데이터
INSERT INTO orders (user_id, total_amount, status, created_at)
VALUES (11, 1580000, 'CONFIRMED', NOW()),
       (2, 880000, 'SHIPPED', NOW()),
       (3, 750000, 'DELIVERED', NOW()),
       (4, 550000, 'CANCELLED', NOW()),
       (5, 400000, 'CANCELLED', NOW()),
       (6, 350000, 'PENDING', NOW()),
       (7, 280000, 'CONFIRMED', NOW()),
       (8, 170000, 'PENDING', NOW()),
       (9, 130000, 'SHIPPED', NOW()),
       (10, 80000, 'DELIVERED',NOW());

-- 주문 상품 데이터 (order_items)
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES
-- 주문 1 (김철수): 노트북 + 마우스
(1, 1, 1, 1500000),
(1, 5, 1, 80000),

-- 주문 2 (이영희): 스마트폰 + 마우스
(2, 2, 1, 800000),
(2, 5, 1, 80000),

-- 주문 3 (박민수): 태블릿 + 키보드
(3, 3, 1, 600000),
(3, 4, 1, 150000),

-- 주문 4 (최지영): 모니터 + 키보드
(4, 6, 1, 400000),
(4, 4, 1, 150000),

-- 주문 5 (정다은): 모니터
(5, 6, 1, 400000),

-- 주문 6 (홍길동): 스피커 + 충전기
(6, 9, 1, 300000),
(6, 10, 1, 50000),

-- 주문 7 (김민지): 헤드셋 + 마우스
(7, 7, 1, 200000),
(7, 5, 1, 80000),

-- 주문 8 (이준호): 키보드 + 마우스
(8, 4, 1, 150000),
(8, 5, 1, 20000),

-- 주문 9 (박서연): 웹캠 + 충전기
(9, 8, 1, 120000),
(9, 10, 1, 10000),

-- 주문 10 (최윤석): 마우스
(10, 5, 1, 80000);
