plugins {
    val springBootVersion = "2.7.13"
    val kotlinVersion = "1.9.24"
    val dependencyVersion = "1.1.4"
    val lombokVersion = "8.1.0"

    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version dependencyVersion

    kotlin("jvm") version kotlinVersion // Kotlin을 JVM 바이트코드로 컴파일하는데 필요
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.lombok") version kotlinVersion // Lombok을 Kotlin에서 사용가능하도록 도와줌
    id("io.freefair.lombok") version lombokVersion // Lombok을 프로젝트에 쉽게 통합할 수 있도록 도와줌
}

group = "io.junseok"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    val mockkVersion = "1.13.8"
    val kotestVersion = "5.8.0"
    //query dsl
    val querydslVersion = "5.0.0"
    implementation("com.querydsl:querydsl-jpa:$querydslVersion")
    kapt("com.querydsl:querydsl-apt:$querydslVersion:jpa")

    //jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    //security
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")
    implementation("com.auth0:java-jwt:3.18.1")

    //test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation ("org.springframework.security:spring-security-test")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")

    //etc
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    kapt("org.projectlombok:lombok")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //mysql
    runtimeOnly("com.mysql:mysql-connector-j")

    //swagger
    implementation("org.springdoc:springdoc-openapi-ui:1.5.12")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.5.12")


    //feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:3.1.8")
    implementation("io.github.openfeign:feign-jackson:12.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("io.github.openfeign:feign-okhttp:11.8")

    implementation ("org.springframework.boot:spring-boot-starter-cache")

    //fcm
    implementation ("com.google.firebase:firebase-admin:9.1.1")
}
allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

//  kapt가 Java 애노테이션 프로세서를 유지
kapt {
    keepJavacAnnotationProcessors = true
}

tasks {
    test {
        useJUnitPlatform()
    }
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}

