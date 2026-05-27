package com.example.plugins

import com.example.config.DatabaseConfig
import com.example.config.createDataSource
import com.example.config.runMigrations
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase(dbConfig: DatabaseConfig) {
    val dataSource = createDataSource(dbConfig)

    runMigrations(dataSource)

    Database.connect(dataSource)

    transaction {
        println("Database connected: ${dbConfig.url}")
    }
}