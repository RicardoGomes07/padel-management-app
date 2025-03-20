package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class CourtCreationInput(
    val cid: UInt,
    val name: String,
)
