package pt.isel.ls.webApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class CourtsOutput(
    val courts: List<CourtDetailsOutput>,
)
