package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClubsOutput(
    val clubs: List<ClubDetailsOutput>,
)
