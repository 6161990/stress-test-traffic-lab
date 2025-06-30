package com.yoon.stress.config

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@Configuration
class TestContainerConfig {
    
    @Bean
    @ServiceConnection
    fun postgreSQLContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer(DockerImageName.parse("postgres:15"))
            .withDatabaseName("stress_test")
            .withUsername("test")
            .withPassword("test")
    }
}