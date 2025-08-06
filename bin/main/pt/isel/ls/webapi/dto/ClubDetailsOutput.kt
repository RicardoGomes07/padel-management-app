package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Club

@Serializable
data class ClubDetailsOutput(
    val cid: UInt,
    val name: String,
    val owner: OwnerOutput,
) {
    constructor(club: Club) : this (
        club.cid,
        club.name.value,
        OwnerOutput(club.owner.name.value, club.owner.uid),
    )
}
