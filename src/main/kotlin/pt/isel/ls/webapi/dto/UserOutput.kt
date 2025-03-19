package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserOutput(
    val name: String,
    val token: String,
)
