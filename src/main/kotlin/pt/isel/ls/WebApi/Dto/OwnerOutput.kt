package pt.isel.ls.WebApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class OwnerOutput(
    val name: String,
    val uid: Int,
)