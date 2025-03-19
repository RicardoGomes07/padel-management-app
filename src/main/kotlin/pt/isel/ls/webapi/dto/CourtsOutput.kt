package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class CourtsOutput(
    val courts: List<CourtDetailsOutput>,
)
