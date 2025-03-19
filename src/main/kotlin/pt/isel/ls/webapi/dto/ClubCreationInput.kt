package pt.isel.ls.webApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class ClubCreationInput(
    val name: String,
    val owner: OwnerOutput,
)
