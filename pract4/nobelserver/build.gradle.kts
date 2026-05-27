
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    kotlin("plugin.serialization") version "2.0.21"
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}
dependencies {
    implementation(ktorLibs.server.auth)
    implementation(ktorLibs.server.auth.jwt)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(libs.logback.classic)

    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    implementation("com.auth0:java-jwt:4.4.0")

    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.6")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.6")

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}
