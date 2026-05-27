package com.example.domain.model

import java.time.Instant

data class UserPrize(
    val userId: Int,
    val prizeId: Int,
    val addedAt: Instant = Instant.now()
)