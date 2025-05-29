package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserCreationInput(
    val name: String,
    val email: String,
    val password: String,
)
