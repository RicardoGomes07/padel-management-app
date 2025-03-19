package pt.isel.ls.webApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClubCreationInput(
    val name: String,
    val owner: OwnerOutput,
)
