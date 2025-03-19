package pt.isel.ls.webApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserInput(
    val name: String,
    val email: String,
)
