package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProblemOutput(
    val title: String,
    val description: String,
)
