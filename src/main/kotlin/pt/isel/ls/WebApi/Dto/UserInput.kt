package pt.isel.ls.WebApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class UserInput(
    val name: String,
    val email: String,
)