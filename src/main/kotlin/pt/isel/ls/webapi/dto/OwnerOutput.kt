package pt.isel.ls.webApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class OwnerOutput(
    val name: String,
    val uid: Int,
)
