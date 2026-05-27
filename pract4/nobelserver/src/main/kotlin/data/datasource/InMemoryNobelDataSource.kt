package com.example.data.datasource

import com.example.domain.model.Laureate
import com.example.domain.model.NobelPrize
import com.example.domain.model.User

class InMemoryNobelDataSource {

    private val prizes: List<NobelPrize> = listOf(
        NobelPrize(
            year = 2023,
            category = "physics",
            categoryFullName = "Nobel Prize in Physics",
            overallMotivation = "for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter",
            laureates = listOf(
                Laureate(
                    "1001",
                    "Pierre",
                    "Agostini",
                    "for experimental methods that generate attosecond pulses of light"
                ),
                Laureate("1002", "Ferenc", "Krausz", "for experimental methods that generate attosecond pulses of light"),
                Laureate("1003", "Anne", "L'Huillier", "for experimental methods that generate attosecond pulses of light")
            )
        ),
        NobelPrize(
            year = 2023,
            category = "chemistry",
            categoryFullName = "Nobel Prize in Chemistry",
            overallMotivation = "for the discovery and development of quantum dots",
            laureates = listOf(
                Laureate("2001", "Moungi G.", "Bawendi", "for the discovery and development of quantum dots"),
                Laureate("2002", "Louis E.", "Brus", "for the discovery and development of quantum dots"),
                Laureate("2003", "Alexei I.", "Ekimov", "for the discovery and development of quantum dots")
            )
        ),
        NobelPrize(
            year = 2023,
            category = "medicine",
            categoryFullName = "Nobel Prize in Physiology or Medicine",
            overallMotivation = "for their discoveries concerning nucleoside base modifications that enabled the development of effective mRNA vaccines against COVID-19",
            laureates = listOf(
                Laureate("3001", "Katalin", "Karikó", "for discoveries concerning nucleoside base modifications"),
                Laureate("3002", "Drew", "Weissman", "for discoveries concerning nucleoside base modifications")
            )
        ),
        NobelPrize(
            year = 2022,
            category = "peace",
            categoryFullName = "Nobel Peace Prize",
            overallMotivation = "for their efforts to document war crimes and human rights violations",
            laureates = listOf(
                Laureate("4001", "Ales", "Bialiatski", "for efforts to document war crimes"),
                Laureate("4002", "Memorial", "", "for efforts to document war crimes"),
                Laureate("4003", "Center for Civil Liberties", "", "for efforts to document war crimes")
            )
        )
    )

    private val users: List<User> = User.testUsers()

    fun getAllPrizes(): List<NobelPrize> = prizes

    fun getPrizeByYearAndCategory(year: Int, category: String): NobelPrize? {
        return prizes.find { it.year == year && it.category.equals(category, ignoreCase = true) }
    }

    fun getLaureates(year: Int, category: String): List<Laureate>? {
        return getPrizeByYearAndCategory(year, category)?.laureates
    }

    fun findUser(username: String): User? {
        return users.find { it.username == username }
    }
}