package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Club

@Serializable
data class ClubsOutput(
    val clubs: List<ClubOutput>,
)

fun List<Club>.toClubsOutput() = ClubsOutput(this.map { ClubOutput(it) })
