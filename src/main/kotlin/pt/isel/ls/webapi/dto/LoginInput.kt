package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginInput(
    val email: String,
    val password: String,
)
