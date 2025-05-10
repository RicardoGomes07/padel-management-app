package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Court

@Serializable
data class CourtOutput(
    val crid: UInt,
    val name: String,
    val cid: UInt,
) {
    constructor(court: Court) :
        this(
            court.crid,
            court.name.value,
            court.club.cid,
        )
}
