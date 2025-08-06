package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Court

@Serializable
data class CourtDetailsOutput(
    val crid: UInt,
    val name: String,
    val club: ClubDetailsOutput,
) {
    constructor(court: Court) :
        this(
            court.crid,
            court.name.value,
            ClubDetailsOutput(court.club),
        )
}
