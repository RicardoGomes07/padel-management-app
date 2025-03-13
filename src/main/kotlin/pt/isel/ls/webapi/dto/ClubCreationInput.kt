package pt.isel.ls.WebApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class ClubCreationInput (
    val name: String,
    val owner: OwnerOutput,
)