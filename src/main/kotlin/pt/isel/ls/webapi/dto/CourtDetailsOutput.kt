package pt.isel.ls.webApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class CourtDetailsOutput(
    val crid: Int,
    val name: String,
    val club: ClubDetailsOutput,
)
