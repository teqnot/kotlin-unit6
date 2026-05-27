package com.example.authapp.data.model

import com.example.authapp.domain.model.User
import kotlinx.serialization.Serializable
@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val image: String,
    val birthDate: String? = null,
    val phone: String? = null
)

fun UserDto.toDomain() = User(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    fullName = "$firstName $lastName",
    gender = gender,
    image = image,
    birthDate = birthDate,
    phone = phone
)

@Serializable
data class UsersResponse(
    val users: List<UserDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)