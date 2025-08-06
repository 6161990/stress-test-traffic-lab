package com.yoon.stress.simulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class OrderSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    private final FeederBuilder<String> orderFeeder = csv("order.csv").random();

    private final ScenarioBuilder scn = scenario("Order API Performance Test")
        .feed(orderFeeder)

            // 1. 특정 사용자 주문 조회
        .exec(
            http("Get User Orders")
                .get("/api/orders/user/#{userId}")
                .check(status().is(200))
        )
        // 2. 새 주문 생성
        .exec(
            http("Create Order")
                .post("/api/orders")
                .body(StringBody("""
                    {
                        "userId": #{userId},
                        "items" : [
                            "productId": #{productId},
                            "quantity": 2
                        ]
                    }
                """)).asJson()
                .check(status().is(201))
//                .check(jsonPath("$.id").saveAs("newOrderId"))
        );

    {
        setUp(
            scn.injectOpen(
                rampUsers(50).during(Duration.ofSeconds(30)),
                constantUsersPerSec(10).during(Duration.ofSeconds(60))
            ).protocols(httpProtocol)
        ).assertions(
            global().responseTime().max().lt(5000),
            global().successfulRequests().percent().gt(95.0)
        );
    }
}
