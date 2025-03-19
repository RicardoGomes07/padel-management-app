package pt.isel.ls.webApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProblemOutput(
    val title: String,
    val description: String,
)
