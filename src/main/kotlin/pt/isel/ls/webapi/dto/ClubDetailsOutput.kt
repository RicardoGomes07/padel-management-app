package pt.isel.ls.webApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class ClubDetailsOutput(
    val cid: Int,
    val name: String,
    val owner: OwnerOutput,
)
