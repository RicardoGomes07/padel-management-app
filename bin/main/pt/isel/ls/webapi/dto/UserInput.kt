package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserInput(
    val name: String,
    val email: String,
)
