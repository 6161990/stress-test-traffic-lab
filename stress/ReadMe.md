# JMeter 스트레스 테스트 실행 가이드

## 프로젝트 개요
Spring Boot 기반 전자상거래 API의 성능 테스트를 위한 JMeter 스트레스 테스트 환경입니다.

## 스크립트 파일 설명
- `api-stress-test.jmx`: 전체 API 테스트 스크립트 (Product, User, Order API)

## 사전 준비

### 1. 애플리케이션 빌드
```shell
./gradlew clean build -x test
```

### 2. Docker 이미지 빌드 및 전체 서비스 실행
```shell
docker-compose up -d --build
```

```shell
docker-compose down
```

### 3. 애플리케이션 상태 확인
```shell
curl http://localhost:8080/actuator/health
```

**정상 응답 예시:**
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

## JMeter 스트레스 테스트 실행

### 1. JMeter 스크립트 파일을 Docker 컨테이너로 복사
```shell
docker cp api-stress-test.jmx stress-jmeter:/jmeter/api-stress-test.jmx
```

### 2. JMeter 테스트 실행
```shell
docker exec stress-jmeter jmeter -n -t /jmeter/api-stress-test.jmx -l /jmeter/api-result.jtl
```

### 3. 결과 리포트 생성

#### HTML 리포트 생성
```shell
docker exec stress-jmeter mkdir -p /tmp/jmeter-report
docker exec stress-jmeter jmeter -g /jmeter/api-result.jtl -o /tmp/jmeter-report/
```

#### 결과 파일 호스트로 복사
```shell
docker cp stress-jmeter:/jmeter/api-result.jtl ./api-result.jtl
docker cp stress-jmeter:/tmp/jmeter-report ./jmeter-report
```

#### 리포트 확인
```shell
open jmeter-report/index.html
```

## 테스트 시나리오

### API 스트레스 테스트 (api-stress-test.jmx)

#### Product API (20개 스레드, 3회 반복)
- `GET /api/products` - 전체 상품 조회
- `GET /api/products/{id}` - 특정 상품 조회
- `GET /api/products/in-stock` - 재고 있는 상품 조회
- `GET /api/products/search?name={name}` - 상품 이름으로 검색
- `POST /api/products` - 새 상품 생성

#### User API (10개 스레드, 2회 반복)
- `GET /api/users` - 전체 사용자 조회
- `GET /api/users/{id}` - 특정 사용자 조회
- `POST /api/users` - 새 사용자 생성

#### Order API (5개 스레드, 2회 반복)
- `GET /api/orders/user/{userId}` - 사용자별 주문 조회
- `POST /api/orders` - 새 주문 생성

#### Health Check (5개 스레드, 1회 실행)
- `GET /actuator/health` - 애플리케이션 상태 확인

## 테스트 데이터

### 사전 생성된 데이터
- **사용자**: 10명 (ID: 1-10)
- **상품**: 15개 (ID: 1-15)
- **주문**: 10개 (ID: 1-10)
- **주문 상품**: 각 주문당 1-2개 상품

### 동적 생성 데이터
JMeter 테스트 중 랜덤 데이터로 생성:
- 새 상품 (가격: 10-100원, 재고: 1-50개)
- 새 사용자 (나이: 18-65세)
- 새 주문 (기존 사용자/상품 ID 참조)

## 설정 변경

### JMeter 스크립트 설정
테스트 파라미터는 JMeter 스크립트의 User Defined Variables에서 변경 가능:
- `host`: 테스트 대상 호스트 (기본값: stress)
- `port`: 테스트 대상 포트 (기본값: 8080)

### 로드 테스트 설정 변경
스레드 수와 반복 횟수는 각 Thread Group에서 수정:
1. JMeter GUI로 스크립트 열기
2. Thread Group 선택
3. Number of Threads, Loop Count 수정

## 개발 워크플로우

### 코드 변경 후 재배포
```shell
# 1. 애플리케이션 빌드
./gradlew clean build -x test

# 2. Docker 서비스 중지
docker-compose down

# 3. 새 이미지로 재빌드 및 실행
docker-compose up -d --build

# 4. 상태 확인
curl http://localhost:8080/actuator/health
```

### 로컬 개발 환경
IntelliJ에서 개발할 때는 포트 충돌 방지를 위해:
```shell
# Docker 애플리케이션 중지
docker-compose down

# 또는 로컬 프로파일 사용 (8081 포트)
--spring.profiles.active=local
```

### 로그 확인
```shell
# 애플리케이션 로그
docker logs stress --tail 50

# 데이터베이스 로그
docker logs stress-db --tail 50

# 실시간 로그
docker logs -f stress
```

### 컨테이너 상태 확인
```shell
docker-compose ps
```

## 성능 지표 해석

### 주요 메트릭
- **Throughput (처리율)**: 초당 처리된 요청 수
- **Response Time (응답시간)**: 평균/최소/최대 응답 시간
- **Error Rate (오류율)**: 실패한 요청의 비율
- **95th Percentile**: 95%의 요청이 이 시간 이내에 처리됨

### 정상 기준값 (참고)
- 평균 응답시간: < 200ms
- 95th Percentile: < 500ms
- 오류율: < 5%
- 처리율: > 50 TPS

## 문제 해결

### 일반적인 문제들

#### 1. 데이터베이스 연결 실패
```shell
# 해결방법
docker-compose down
docker-compose up -d --build
```

#### 2. 포트 충돌 (8080)
```shell
# 실행 중인 프로세스 확인
lsof -i :8080

# Docker 애플리케이션 중지
docker-compose down
```

#### 3. JMeter 스크립트 파일 없음
```shell
# 파일 복사 재시도
docker cp api-stress-test.jmx stress-jmeter:/jmeter/api-stress-test.jmx
```

#### 4. 메모리 부족
```shell
# Docker Desktop 메모리 할당 증가 (최소 4GB 권장)
# 또는 테스트 스레드 수 감소
```

## 주의사항
- 애플리케이션이 완전히 시작된 후 테스트 실행
- 데이터베이스 헬스체크 통과 확인
- 충분한 시스템 리소스 확보 (메모리 4GB+ 권장)
- 코드 변경 후 반드시 `./gradlew clean build -x test` 실행
- 테스트 결과는 시스템 사양에 따라 달라질 수 있음

## 추가 리소스
- [JMeter 공식 문서](https://jmeter.apache.org/usermanual/index.html)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Docker Compose 문서](https://docs.docker.com/compose/)
