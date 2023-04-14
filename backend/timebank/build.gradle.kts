import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("io.spring.gradle:dependency-management-plugin:1.1.0")
    }
}

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
    kotlin("plugin.allopen") version "1.7.22"
}

group = "kookmin.software.capstone2023"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}


dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.1")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.security:spring-security-core")
    implementation("com.auth0:java-jwt:4.3.0")

    runtimeOnly("mysql:mysql-connector-java:8.0.32")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Plain Jar 파일이 생성되지 않도록 Jar Task 비활성화
tasks.jar {
    enabled = false
}