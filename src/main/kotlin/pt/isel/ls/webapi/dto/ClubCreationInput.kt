package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClubCreationInput(
    val name: String,
    val owner: OwnerOutput,
)
