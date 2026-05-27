package com.example.data.repository

import com.example.data.database.LaureateEntity
import com.example.data.database.Laureates
import com.example.data.database.PrizeEntity
import com.example.data.database.Prizes
import com.example.data.database.UserEntity
import com.example.data.database.UserPrizes
import com.example.data.database.Users
import com.example.data.external.NobelApiClient
import com.example.data.external.toDomainPrize
import com.example.domain.model.Laureate
import com.example.domain.model.Prize
import com.example.domain.model.User
import com.example.domain.repository.NobelRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class NobelRepositoryImpl : NobelRepository {

    override suspend fun findUserByUsername(username: String): User? = transaction {
        UserEntity.find { Users.username eq username }.firstOrNull()?.toDomain()
    }

    override suspend fun createUser(user: User): User = transaction {
        val entity = UserEntity.new {
            this.username = user.username
            this.passwordHash = BCrypt.hashpw(user.passwordHash, BCrypt.gensalt())
            this.role = user.roles.firstOrNull() ?: "user"
        }
        entity.toDomain()
    }

    override suspend fun getAllPrizes(): List<Prize> = transaction {
        PrizeEntity.all().map { it.toDomain() }
    }

    override suspend fun getPrizeByYearAndCategory(year: Int, category: String): Prize? = transaction {
        PrizeEntity.find { (Prizes.awardYear eq year) and (Prizes.category eq category) }
            .firstOrNull()?.toDomain()
    }

    override suspend fun savePrize(prize: Prize): Prize = transaction {
        val entity = PrizeEntity.find { (Prizes.awardYear eq prize.awardYear) and (Prizes.category eq prize.category) }
            .firstOrNull() ?: PrizeEntity.new {
            this.awardYear = prize.awardYear
            this.category = prize.category
            this.fullName = prize.fullName
            this.motivation = prize.motivation
            this.detailLink = prize.detailLink
        }
        entity.toDomain()
    }

    override suspend fun syncPrizesFromExternal(): Int {
        return try {
            val externalPrizes = NobelApiClient.fetchPrizes()
            var savedCount = 0

            externalPrizes.forEach { extPrize ->
                try {
                    val prize = extPrize.toDomainPrize()

                    val savedPrize = savePrize(prize)

                    val laureates = extPrize.laureates.mapNotNull { extLaureate ->
                        Laureate(
                            prizeId = savedPrize.id ?: return@mapNotNull null,
                            fullName = extLaureate.fullName,
                            portion = extLaureate.portion ?: "1",
                            motivation = extLaureate.motivation?.get("en"),
                            portraitUrl = null
                        )
                    }

                    if (laureates.isNotEmpty()) {
                        saveLaureates(laureates)
                    }

                    savedCount++

                } catch (e: Exception) {
                    println("⚠Failed to save prize ${extPrize.awardYear}/${extPrize.categoryEnglish}: ${e.message}")
                }
            }

            println("Synced $savedCount prizes from external API")
            savedCount

        } catch (e: Exception) {
            println("Sync failed: ${e.message}")
            0
        }
    }

    override suspend fun getLaureatesByPrize(prizeId: Int): List<Laureate> = transaction {
        LaureateEntity.find { Laureates.prize eq prizeId }.map { it.toDomain() }
    }

    override suspend fun saveLaureates(laureates: List<Laureate>): List<Laureate> = transaction {
        laureates.map { laureate ->
            val existing = LaureateEntity.find {
                (Laureates.prize eq laureate.prizeId) and (Laureates.fullName eq laureate.fullName)
            }.firstOrNull()

            if (existing == null) {
                LaureateEntity.new {
                    this.prize = PrizeEntity[laureate.prizeId]
                    this.fullName = laureate.fullName
                    this.portion = laureate.portion
                    this.motivation = laureate.motivation
                    this.portraitUrl = laureate.portraitUrl
                }.toDomain()
            } else {
                existing.toDomain()
            }
        }
    }

    override suspend fun getUserFavorites(userId: Int): List<Prize> = transaction {
        (UserPrizes innerJoin Prizes)
            .slice(Prizes.columns)
            .select { UserPrizes.userId eq userId }
            .map { row ->
                Prize(
                    id = row[Prizes.id].value,
                    awardYear = row[Prizes.awardYear],
                    category = row[Prizes.category],
                    fullName = row[Prizes.fullName],
                    motivation = row[Prizes.motivation],
                    detailLink = row[Prizes.detailLink],
                    laureates = emptyList()
                )
            }
    }

    override suspend fun addFavorite(userId: Int, prizeId: Int): Boolean = transaction {
        try {
            UserPrizes.insert {
                it[UserPrizes.userId] = userId
                it[UserPrizes.prizeId] = prizeId
            }
            true
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            false
        }
    }

    override suspend fun removeFavorite(userId: Int, prizeId: Int): Boolean = transaction {
        UserPrizes.deleteWhere {
            (UserPrizes.userId eq userId) and (UserPrizes.prizeId eq prizeId)
        } > 0
    }

    override suspend fun isFavorite(userId: Int, prizeId: Int): Boolean = transaction {
        UserPrizes.select {
            (UserPrizes.userId eq userId) and (UserPrizes.prizeId eq prizeId)
        }.empty().not()
    }

    override suspend fun authenticate(username: String, password: String): User? {
        val user = findUserByUsername(username) ?: return null
        return if (BCrypt.checkpw(password, user.passwordHash)) {
            user
        } else null
    }

    override suspend fun getLaureatesByYearAndCategory(year: Int, category: String): List<Laureate>? = transaction {
        val prize = Prizes.select { (Prizes.awardYear eq year) and (Prizes.category eq category) }
            .firstOrNull() ?: return@transaction null

        Laureates.select { Laureates.prize eq prize[Prizes.id] }
            .map { row ->
                Laureate(
                    id = row[Laureates.id].value,
                    prizeId = row[Laureates.prize].value,
                    fullName = row[Laureates.fullName],
                    portion = row[Laureates.portion],
                    motivation = row[Laureates.motivation],
                    portraitUrl = row[Laureates.portraitUrl]
                )
            }
    }
}

private fun PrizeEntity.toDomain(): Prize {
    return Prize(
        id = this.id.value,
        awardYear = this.awardYear,
        category = this.category,
        fullName = this.fullName,
        motivation = this.motivation,
        detailLink = this.detailLink,
        laureates = this.laureates.map { it.toDomain() }
    )
}

private fun LaureateEntity.toDomain(): Laureate {
    return Laureate(
        id = this.id.value,
        prizeId = this.prize.id.value,
        fullName = this.fullName,
        portion = this.portion,
        motivation = this.motivation,
        portraitUrl = this.portraitUrl
    )
}