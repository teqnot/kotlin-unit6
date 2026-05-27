
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    kotlin("plugin.serialization") version "2.0.21"
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

application {
    mainClass.set("com.example.ApplicationKt")
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources")
        }
    }
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

    implementation("io.ktor:ktor-server-swagger-jvm:2.3.6")

    implementation("org.jetbrains.exposed:exposed-core:0.43.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.43.0")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("org.flywaydb:flyway-core:9.22.3")

    implementation("io.ktor:ktor-client-core-jvm:2.3.6")
    implementation("io.ktor:ktor-client-cio-jvm:2.3.6")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:2.3.6")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.6")

    implementation("org.mindrot:jbcrypt:0.4")

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}
