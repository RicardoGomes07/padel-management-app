package pt.isel.ls.webApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class CourtCreationInput(
    val name: String,
    val cid: Int,
)
