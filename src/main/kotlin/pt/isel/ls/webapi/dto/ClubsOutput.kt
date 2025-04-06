package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Club

@Serializable
data class ClubsOutput(
    val clubs: List<ClubOutput>,
    val paginationInfo: PaginationInfo,
)

fun List<Club>.toClubsOutput(paginationInfo: PaginationInfo) = ClubsOutput(this.map { ClubOutput(it) }, paginationInfo)
