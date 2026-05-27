package com.example.data.database

import com.example.domain.model.User
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Users : IntIdTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20).default("user")
    val createdAt = timestamp("created_at").clientDefault { java.time.Instant.now() }
    val updatedAt = timestamp("updated_at").clientDefault { java.time.Instant.now() }
}

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var username by Users.username
    var passwordHash by Users.passwordHash
    var role by Users.role
    var createdAt by Users.createdAt
    var updatedAt by Users.updatedAt

    fun toDomain(): User {
        return User(
            id = this.id.value,
            username = this.username,
            passwordHash = this.passwordHash,
            roles = listOf(this.role)
        )
    }
}

object Prizes : IntIdTable("prizes") {
    val awardYear = integer("award_year")
    val category = varchar("category", 50)
    val fullName = varchar("full_name", 255)
    val motivation = text("motivation").nullable()
    val detailLink = varchar("detail_link", 500).nullable()
    val createdAt = timestamp("created_at").clientDefault { java.time.Instant.now() }
    val updatedAt = timestamp("updated_at").clientDefault { java.time.Instant.now() }

    init {
        uniqueIndex(awardYear, category)
        index(false, awardYear, category)
    }
}

class PrizeEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PrizeEntity>(Prizes)

    var awardYear by Prizes.awardYear
    var category by Prizes.category
    var fullName by Prizes.fullName
    var motivation by Prizes.motivation
    var detailLink by Prizes.detailLink
    var createdAt by Prizes.createdAt
    var updatedAt by Prizes.updatedAt

    val laureates by LaureateEntity referrersOn Laureates.prize
}

object Laureates : IntIdTable("laureates") {
    val prize = reference("prize_id", Prizes, onDelete = org.jetbrains.exposed.sql.ReferenceOption.CASCADE)
    val fullName = varchar("full_name", 255)
    val portion = varchar("portion", 10).default("1")
    val motivation = text("motivation").nullable()
    val portraitUrl = varchar("portrait_url", 500).nullable()
    val createdAt = timestamp("created_at").clientDefault { java.time.Instant.now() }

    init {
        uniqueIndex(prize, fullName)
        index(false, prize)
    }
}

class LaureateEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<LaureateEntity>(Laureates)

    var prize by PrizeEntity referencedOn Laureates.prize
    var fullName by Laureates.fullName
    var portion by Laureates.portion
    var motivation by Laureates.motivation
    var portraitUrl by Laureates.portraitUrl
    var createdAt by Laureates.createdAt
}

object UserPrizes : Table("user_prizes") {
    val userId = reference("user_id", Users, onDelete = org.jetbrains.exposed.sql.ReferenceOption.CASCADE)
    val prizeId = reference("prize_id", Prizes, onDelete = org.jetbrains.exposed.sql.ReferenceOption.CASCADE)
    val addedAt = timestamp("added_at").clientDefault { java.time.Instant.now() }

    override val primaryKey = PrimaryKey(userId, prizeId)

    init {
        index(false, userId)
    }
}