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
                        "stock": 100
                        "description": "Test product description"
                    }
                """)).asJson()
                .check(status().is(201))
//                .check(jsonPath("$.id").saveAs("newProductId"))
        );

    {
        setUp(
            scn.injectOpen(
                atOnceUsers(20),
                rampUsers(80).during(Duration.ofSeconds(60)),
                constantUsersPerSec(15).during(Duration.ofSeconds(120))
            ).protocols(httpProtocol)
        ).assertions(
            global().responseTime().max().lt(3000),
            global().responseTime().mean().lt(1000),
            global().successfulRequests().percent().gt(98.0),
            forAll().failedRequests().count().lt(10L)
        );
    }
}
