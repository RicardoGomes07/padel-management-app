package pt.isel.ls.webApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserOutput(
    val name: String,
    val token: String,
)
