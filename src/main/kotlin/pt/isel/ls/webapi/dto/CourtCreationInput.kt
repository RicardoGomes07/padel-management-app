package pt.isel.ls.webApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class CourtCreationInput(
    val name: String,
    val cid: Int,
)
