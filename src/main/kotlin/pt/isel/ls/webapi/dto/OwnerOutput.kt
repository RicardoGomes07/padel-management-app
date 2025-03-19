package pt.isel.ls.webApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class OwnerOutput(
    val name: String,
    val uid: Int,
)
