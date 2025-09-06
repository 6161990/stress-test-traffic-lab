package com.yoon.stress.simulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ProductSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    // 상품 데이터 피더
    private final FeederBuilder<String> productFeeder = csv("products.csv").random();

    private final ScenarioBuilder scn = scenario("Product API Performance Test")
        .feed(productFeeder)
        // 1. 전체 상품 목록 조회
        .exec(
            http("Get All Products")
                .get("/api/products")
                .check(status().is(200))
        )
        // 2. 상품 검색
        .exec(
            http("Search Products")
                .get("/api/products/search?name=#{name}")
                .check(status().is(200))
        )
        // 3. 특정 상품 조회
        .exec(
            http("Get Products By Id")
                .get("/api/products/#{id}")
                .check(status().is(200))
        )
        // 4. 특정 상품 상세 조회
        .exec(
            http("Get Product Detail")
                .get("/api/products/price-range?minPrice=#{minPrice}&maxPrice=#{maxPrice}")
                .check(status().is(200))
                // .check(jsonPath("$.id").saveAs("currentProductId"))
        )
        // 5. 상품 재고 확인
        .exec(
            http("Check Product Stock")
                .get("/api/products/in-stock")
                .check(status().is(200))
        )
        // 6. 새 상품 등록
        .exec(
            http("Create Product")
                .post("/api/products")
                .body(StringBody("""
                    {
                        "name": "New Product #{randomInt}",
                        "price": 19000,
                        "stock": 100,
                        "description": "Test product description"
                    }
                """)).asJson()
                .check(status().is(200))
//                .check(jsonPath("$.id").saveAs("newProductId"))
        );

    {
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
            ).protocols(httpProtocol)
                /**
                 ,
                 *  injectClosed : Closed Workload (동시 접속자 수 기반)
                 *  동시에 몇 명이 유지되느냐에 집중, 서버 내부에서 동시 처리 세션 수를 유지하는 상황 시뮬레이션

            scn.injectClosed(
                constantConcurrentUsers(50).during(Duration.ofSeconds(60))  // 동시 사용자 50명 유지 : 50 * 0.2(RT) => 250 req/s
            ).protocols(httpProtocol)     * */
        ).assertions(
            global().responseTime().max().lt(3000),
            global().responseTime().mean().lt(1000),
            global().successfulRequests().percent().gt(98.0),
            forAll().failedRequests().count().lt(10L)
        );
    }
}
