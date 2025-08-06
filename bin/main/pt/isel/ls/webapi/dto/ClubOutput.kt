package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Club

@Serializable
data class ClubOutput(
    val cid: UInt,
    val name: String,
) {
    constructor(club: Club) : this (
        club.cid,
        club.name.value,
    )
}
