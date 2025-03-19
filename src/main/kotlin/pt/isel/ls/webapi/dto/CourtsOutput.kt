package pt.isel.ls.webApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class CourtsOutput(
    val courts: List<CourtDetailsOutput>,
)
