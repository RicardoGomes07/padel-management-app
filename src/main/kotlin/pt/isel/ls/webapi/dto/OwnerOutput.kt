package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class OwnerOutput(
    val name: String,
    val uid: Int,
)
