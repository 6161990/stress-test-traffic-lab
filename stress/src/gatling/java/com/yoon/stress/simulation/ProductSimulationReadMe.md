### Question

```
setUp(
            /**
             *  injectOpen : Open Workload (트래픽 유입 속도 기반) => 트래픽 유입량(RPS) 제어
             *  초당 몇 명이 유입 되느냐에 집중하며 외부에서 유저들이 계속 새로 유입되는 상황을 시뮬레이션 할 수 있다.
             * */
            scn.injectOpen(
                atOnceUsers(20), // 즉시 20 call 주입 : 1초 안에 모두 처리된다면 20 * 6 => 120 req/s
                rampUsers(80).during(Duration.ofSeconds(60)), // 60초 동안 80 call 주입 : 80/60 = 1.33 * 6 => 8 req/s
                constantUsersPerSec(5).during(Duration.ofSeconds(120)) // 120초 동안 초당 5 유저 신규 주입 : 5 * 6 => 30 req/s
                    /**
                     * 순차대로 120 req/s + 8 req/s + 30 req/s = 158 req/s
                     * */
            ).protocols(httpProtocol),
                /**
                 *  injectClosed : Closed Workload (동시 접속자 수 기반)
                 *  동시에 몇 명이 유지되느냐에 집중, 서버 내부에서 동시 처리 세션 수를 유지하는 상황 시뮬레이션
                 * */
            scn.injectClosed(
                constantConcurrentUsers(50).during(Duration.ofSeconds(60))  // 동시 사용자 50명 유지 : 50 * 0.2(RT) => 250 req/s
            ).protocols(httpProtocol)

```

#### 1. injectOpen 에서 주입된 유저들이 concurrentUser 중 일부인가?
    * nope. injectOpen 과 injectClosed 는 별도로 돌아감.
    * 그래서 injectOpen rampUp 기간을 늘려도 injectClosed 의 concurrent VU 가 50이라면 50은 보장되는 상황이됨.
    * 서버 입장에선 합쳐진 부하임. 순차로 실행하고 싶다면 injectOpen 뒤, andThen() 으로 핸들링할 수 있음.

#### 2. 그럼 injectOpen, injectClose population 의 concurrent VU 는 몇일까?
    * injectOpen 은 RPS(유입율) * 평균 RT(응답시간) 에 따라 달라짐, 하나의 VU 실행이 끝나야 다음 대기 VU 가 인입되므로,
    * injectClose 는 무조건 동시성 세팅 값을 강제.

***현재 ProductSimulation 세팅 값 기준 계산***
평균 RT	  Closed RPS	   Open RPS(평균)	     합계 (대략)
----------------------------------------------------------------------
0.2s	     250	          38	         ~288 rps (+ 시작 스파이크)
0.5s	     100	          38	         ~138 rps (+ 시작 스파이크)
1.0s	     50	              38	         ~88  rps (+ 시작 스파이크)

#### 3. 이건 시나리오 테스트니까 실제 서비스에서 진입 씬이나 페이지별로 시나리오를 정리해서 순차 api 별로 묶는게 나으려나?
    * Gatling 시뮬레이션은 결국 “사용자 시나리오” 단위로 동작하기 때문에,
    * 실제 서비스 환경을 최대한 닮게 만들려면 페이지 진입 흐름이나 기능 플로우 단위로 묶는 게 훨씬 현실적.
    * 시뮬레이션은 도메인 단위가 아니라 시나리오에 따라 분리하는 것이

#### 4. 그럼 내 productSimulation 은 하나의 도메인인 product 에 부하가 몰리는 테스트를 하는거니까 db 나 캐싱 부하테스트에 가까우려나? 시나리오 테스트는 아닌것 같고
    * 지금 ProductSimulation은 product 도메인에만 집중된 엔드포인트 체인이라서 E2E 사용자 여정 시나리오라기보다는 API/DB/캐시 리소스에 부하를 거는 “도메인 집중형 부하 테스트”에 더 가까워.
    * 무엇을 주로 때리게 되냐면, 
        * DB Read 부하: 목록/검색/상세/재고 조회
        * DB Write 부하: 상품 생성
        * 캐시 효과: 같은 ID/쿼리를 반복하면 캐시 히트↑, 랜덤화하면 캐시 미스↑ → DB 부하
   
1) DB/캐시 스트레스 테스트 목적이면
       읽기/쓰기 비율 명시: 예) 90% read, 10% write
       캐시 히트 테스트: 동일 id/name을 반복 → 캐시 적중률, 응답시간 안정성 확인
       캐시 미스/DB 한계 테스트: id/name을 넓게 랜덤 → 인덱스/쿼리 성능 한계 확인
       Think time 추가로 현실성↑ (예: pause(200, 1000) ms)
       Closed 모델로 동시성 고정: injectClosed(constantConcurrentUsers(X))

    캐시 미스 강제하고 싶다면? search?name=#{name}에 feeder로 다양한 키워드 공급
    캐시 히트 강제하고 싶다면? id=1처럼 소수 키 반복
    ```
       val readChain = exec( /* products, search, detail, in-stock */ );
       val writeChain = exec( /* create product */ );
        
       val scn = scenario("Product Read-Heavy")
       .randomSwitch().on(
       Choice.withWeight(90, exec(readChain)),
       Choice.withWeight(10, exec(writeChain))
       )
       .pause(Duration.ofMillis(200), Duration.ofSeconds(1));
    ```
   

2) 사용자 여정(Scenario) 테스트 목적이면
    로그인/메인→목록→검색→상세→장바구니/주문 같은 여러 도메인 묶음으로 플로우화
    각 플로우에 가중치 부여(실제 트래픽 분포 반영)

