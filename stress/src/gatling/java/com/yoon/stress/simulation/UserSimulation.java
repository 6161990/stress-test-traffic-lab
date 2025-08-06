package com.yoon.stress.simulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class UserSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    // 사용자 데이터 피더
    private final FeederBuilder<String> userFeeder = csv("users.csv").circular();

    private final ScenarioBuilder scn = scenario("User API Performance Test")
        .feed(userFeeder)
        // 1. 전체 사용자 목록 조회
        .exec(
            http("Get All Users")
                .get("/api/users")
                .check(status().is(200))
        )
        // 2. 특정 사용자 프로필 조회
        .exec(
            http("Get User")
                .get("/api/users/#{id}")
                .check(status().is(200))
        )
        // 3. 사용자 회원가입
        .exec(
                http("User Registration")
                        .post("/api/users")
                        .body(StringBody("""
                {
                    "email": "#{email}",
                    "name": "#{name}"
                }
            """)).asJson()
                        .check(status().is(201))
//                .check(jsonPath("$.id").saveAs("newUserId"))
        );

    {
        setUp(
            scn.injectOpen(
                rampUsers(30).during(Duration.ofSeconds(45)),
                constantUsersPerSec(8).during(Duration.ofSeconds(90))
            ).protocols(httpProtocol)
        ).assertions(
            global().responseTime().max().lt(4000),
            global().responseTime().percentile3().lt(2000),
            global().successfulRequests().percent().gt(96.0),
            details("User Registration").responseTime().max().lt(3000)
        );
    }
}
