plugins {
    kotlin("jvm") version "1.9.0"
}

group = "lip17"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.4")
    implementation("io.ktor:ktor-server-websockets:2.3.12")
    implementation("io.ktor:ktor-server-core:2.3.12")
    implementation("io.ktor:ktor-server-netty:2.3.12")

    implementation("io.github.oshai:kotlin-logging-jvm:5.1.4")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("org.springframework.kafka:spring-kafka:3.1.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}