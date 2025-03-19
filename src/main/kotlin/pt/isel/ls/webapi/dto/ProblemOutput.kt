package pt.isel.ls.webApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class ProblemOutput(
    val title: String,
    val description: String,
)
