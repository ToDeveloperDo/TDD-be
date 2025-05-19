plugins {
    val springBootVersion = "2.7.13"
    val kotlinVersion = "1.9.24"
    val dependencyVersion = "1.1.4"
    id("com.epages.restdocs-api-spec") version "0.17.1"
    id("jacoco")
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version dependencyVersion

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

jacoco {
    toolVersion = "0.8.10"
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

    //db
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")

    //RabbitMQ
    implementation ("org.springframework.boot:spring-boot-starter-amqp")

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
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
    testImplementation("io.kotest:kotest-framework-datatest-jvm:5.7.2")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.restdocs:spring-restdocs-asciidoctor")

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

    //swagger
    implementation("org.springdoc:springdoc-openapi-ui:1.5.12")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.5.12")

    //feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:3.1.8")
    implementation("io.github.openfeign:feign-jackson:12.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("io.github.openfeign:feign-okhttp:11.8")

    //cache
    implementation ("org.springframework.boot:spring-boot-starter-cache")

    //fcm
    implementation ("com.google.firebase:firebase-admin:9.1.1")

    //h2
    testImplementation("com.h2database:h2:2.1.214")
}
allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

kapt {
    keepJavacAnnotationProcessors = true
}

tasks {
    test {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
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
tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/persistence/entity/**",
                    "**/presentation/**/dto/**",
                    "**/oauth/apple/dto/**",
                    "**/oauth/git/dto/**",
                    "**/io/junseok/todeveloperdo/oauth/apple/service/serviceimpl/ClientSecretCreator.class",
                    "**/io/junseok/todeveloperdo/oauth/apple/service/serviceimpl/ClientSecretCreatorTest.class",
                    "**/io/junseok/todeveloperdo/auth/config/SecurityConfig.class",
                    "**/io/junseok/todeveloperdo/ToDeveloperDoApplicationKt.class",
                    "**/io/junseok/todeveloperdo/oauth/git/service/CustomOAuth2UserService.class"
                )
            }
        })
    )
}

openapi3 {
    this.setServer("https://localhost:8080")
    title = "My API"
    description = "My API description"
    version = "0.1.0"
    format = "yaml"
}

tasks.register<Copy>("copyOasToSwagger") {
    delete("src/main/resources/static/swagger-ui/openapi3.yaml")
    from("$buildDir/api-spec/openapi3.yaml")
    into("src/main/resources/static/swagger-ui/.")
    dependsOn("openapi3")
}