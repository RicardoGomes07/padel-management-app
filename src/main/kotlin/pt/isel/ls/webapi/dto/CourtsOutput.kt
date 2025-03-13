package pt.isel.ls.WebApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class CourtsOutput(
    val courts: List<CourtDetailsOutput>,
)