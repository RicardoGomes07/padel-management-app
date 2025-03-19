package pt.isel.ls.webApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClubsOutput(
    val clubs: List<ClubDetailsOutput>,
)
