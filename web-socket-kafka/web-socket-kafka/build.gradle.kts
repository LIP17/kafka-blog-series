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
    implementation("io.ktor:ktor-server-websockets:3.0.3")
    implementation("io.ktor:ktor-server-core:3.0.3")
    implementation("io.ktor:ktor-server-netty:3.0.3")
    implementation("io.ktor:ktor-client-core:3.0.3")
    implementation("io.ktor:ktor-client-cio:3.0.3")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.0.3")

    implementation("io.github.oshai:kotlin-logging-jvm:5.1.4")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("org.apache.kafka:kafka-clients:3.9.0")

    testImplementation(kotlin("test"))

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}