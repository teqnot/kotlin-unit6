package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.domain.model.User
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.typesafe.config.Config

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val tokenValiditySeconds: Long
) {
    companion object {
        fun fromConfig(config: Config): JwtConfig {
            val authConfig = config.getConfig("auth.jwt")
            return JwtConfig(
                secret = authConfig.getString("secret"),
                issuer = authConfig.getString("issuer"),
                audience = authConfig.getString("audience"),
                realm = authConfig.getString("realm"),
                tokenValiditySeconds = authConfig.getLong("tokenValiditySeconds")
            )
        }
    }
}

fun generateToken(user: User, jwtConfig: JwtConfig): String {
    val algorithm = Algorithm.HMAC256(jwtConfig.secret)

    return JWT.create()
        .withIssuer(jwtConfig.issuer)
        .withAudience(jwtConfig.audience)
        .withSubject(user.username)
        .withClaim("roles", user.roles)
        .withExpiresAt(java.util.Date(System.currentTimeMillis() + jwtConfig.tokenValiditySeconds * 1000))
        .sign(algorithm)
}

fun Application.configureAuthentication(jwtConfig: JwtConfig) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.issuer)
                    .build()
            )

            validate { credential ->
                if (credential.payload.subject != null) {
                    println("JWT valid for user: ${credential.payload.subject}")
                    JWTPrincipal(credential.payload)
                } else {
                    println("JWT invalid: subject is null")
                    null
                }
            }
        }
    }
}