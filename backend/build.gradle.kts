import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version= "1.4.32"))
        classpath(kotlin("noarg", version= "1.4.32"))
    }
}


plugins {
    id("org.springframework.boot") version "2.4.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
    kotlin("plugin.noarg") version "1.4.32"
    kotlin("plugin.jpa") version "1.4.32"
}


springBoot {
    mainClassName = "ru.itmo.idu.admin.Prod"
}

group = "ru.itmo.idu.admin-template"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()

}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:2.2.6.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.apache.commons:commons-collections4:4.4")

    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")
    implementation("jakarta.validation:jakarta.validation-api:2.0.2")


    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.google.guava:guava:28.2-jre")
    implementation("commons-io:commons-io:2.6")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("org.apache.commons:commons-collections4:4.2")
    implementation("mysql:mysql-connector-java:8.0.23")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.2")

    implementation("org.hibernate:hibernate-spatial")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.session:spring-session-core")

    testImplementation("junit:junit:4.13")
    testImplementation("com.h2database:h2:1.4.197")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType < Test > {
    useJUnitPlatform()
}

tasks.withType < KotlinCompile > {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
