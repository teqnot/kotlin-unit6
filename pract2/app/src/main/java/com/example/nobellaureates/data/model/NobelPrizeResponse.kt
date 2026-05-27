package com.example.nobellaureates.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.example.nobellaureates.domain.model.NobelCategory
import com.example.nobellaureates.domain.model.NobelLaureate

@Serializable
data class LaureatesResponse(
    val laureates: List<LaureateDto>
)

@Serializable
data class LaureateDto(
    val id: String,

    val givenName: NameDto? = null,
    val familyName: NameDto? = null,
    val fullName: NameDto? = null,
    val knownName: NameDto? = null,

    val nobelPrizes: List<LaureatePrizeDto>? = null,

    val birth: BirthDto? = null,

    val portrait: PortraitDto? = null,

    val gender: String? = null,
    val fileName: String? = null
) {
    val englishFullName: String
        get() = fullName?.english ?: knownName?.english
        ?: "${givenName?.english ?: ""} ${familyName?.english ?: ""}".trim()
            .takeIf { it.isNotBlank() } ?: "Unknown"

    val birthDate: String?
        get() = birth?.date ?: birth?.year?.let { "$it-00-00" }

    val birthPlace: String?
        get() = birth?.place?.locationString?.english
}

@Serializable
data class BirthDto(
    val date: String? = null,
    val year: String? = null,
    val place: BirthPlaceDto? = null
)

@Serializable
data class BirthPlaceDto(
    val city: NameDto? = null,
    val country: NameDto? = null,
    val locationString: NameDto? = null,
    val latitude: String? = null,
    val longitude: String? = null
)

@Serializable
data class LaureatePrizeDto(
    val awardYear: Int,

    val category: NameDto? = null,

    @SerialName("categoryFullName")
    val categoryFullName: Map<String, String> = emptyMap(),

    val motivation: MotivationDto? = null,
    val prizeStatus: String? = null,
    val portion: String? = null,
    val dateAwarded: String? = null
) {
    val categoryEnglish: String
        get() = category?.english ?: categoryFullName["en"] ?: "Unknown"
}

@Serializable
data class NameDto(
    val en: String? = null,
    val se: String? = null,
    val original: String? = null
) {
    val english: String? get() = en ?: original
}
@Serializable
data class MotivationDto(
    val en: String?
)

@Serializable
data class PortraitDto(
    val url: String?,
    val thumbnailUrl: String?
)

fun LaureateDto.toDomain(): NobelLaureate? {
    val prize = nobelPrizes?.firstOrNull() ?: return null

    return NobelLaureate(
        id = id,
        year = prize.awardYear,

        category = NobelCategory.fromApiString(prize.category?.en ?: prize.categoryFullName["en"] ?: ""),
        categoryName = prize.categoryEnglish,

        fullName = englishFullName,
        motivation = prize.motivation?.en ?: "",
        birthDate = birthDate,
        birthPlace = birthPlace,
        portraitUrl = portrait?.url ?: portrait?.thumbnailUrl
    )
}