package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class CourtCreationInput(
    val name: String,
)
