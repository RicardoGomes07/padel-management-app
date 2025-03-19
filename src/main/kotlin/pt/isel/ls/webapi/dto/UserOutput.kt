package pt.isel.ls.webApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class UserOutput(
    val name: String,
    val token: String,
)
