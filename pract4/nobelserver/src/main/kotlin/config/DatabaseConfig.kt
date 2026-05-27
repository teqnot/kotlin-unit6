package com.example.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.flywaydb.core.Flyway
import javax.sql.DataSource


data class DatabaseConfig(
    val driver: String,
    val url: String,
    val user: String,
    val password: String,
    val poolSize: Int
) {
    companion object {
        fun fromConfig(config: ApplicationConfig): DatabaseConfig {
            val dbConfig = config.config("database")
            return DatabaseConfig(
                driver = dbConfig.property("driver").getString(),
                url = dbConfig.property("url").getString(),
                user = dbConfig.property("user").getString(),
                password = dbConfig.property("password").getString(),
                poolSize = dbConfig.property("poolSize").getString().toInt()
            )
        }
    }
}

fun createDataSource(dbConfig: DatabaseConfig): DataSource {
    return HikariDataSource(HikariConfig().apply {
        driverClassName = dbConfig.driver
        jdbcUrl = dbConfig.url
        username = dbConfig.user
        password = dbConfig.password
        maximumPoolSize = dbConfig.poolSize
        connectionTimeout = 30000
        idleTimeout = 600000
        maxLifetime = 1800000
    })
}

fun runMigrations(dataSource: DataSource) {
    Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:migrations")
        .baselineOnMigrate(true)
        .baselineVersion("1")
        .load()
        .migrate()
}