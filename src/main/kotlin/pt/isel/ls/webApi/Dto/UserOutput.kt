package pt.isel.ls.WebApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class UserOutput(
    val name: String,
    val token: String,
)