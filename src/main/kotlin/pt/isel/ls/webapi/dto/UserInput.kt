package pt.isel.ls.webApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class UserInput(
    val name: String,
    val email: String,
)
