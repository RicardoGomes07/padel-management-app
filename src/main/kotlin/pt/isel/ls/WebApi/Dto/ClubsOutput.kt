package pt.isel.ls.WebApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class ClubsOutput(
    val clubs: List<ClubDetailsOutput>,
)