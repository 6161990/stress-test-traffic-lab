plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("io.gatling.gradle") version "3.10.5"
  id("org.springframework.boot") version "3.5.3"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "com.yoon"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.springframework.boot:spring-boot-testcontainers")
  implementation("org.testcontainers:postgresql")
  runtimeOnly("org.postgresql:postgresql")
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation(kotlin("test"))
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  gatlingImplementation("io.gatling:gatling-app:3.10.5")
  gatlingImplementation("io.gatling:gatling-core:3.10.5")
  gatlingImplementation("io.gatling:gatling-http:3.10.5")
  gatlingImplementation("io.gatling:gatling-core-java:3.10.5")
  gatlingImplementation("io.gatling:gatling-http-java:3.10.5")
}

kotlin {
  jvmToolchain(17)
  compilerOptions {
    freeCompilerArgs.addAll(listOf("-Xjsr305=strict"))
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}


