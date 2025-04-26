package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Court

@Serializable
data class CourtsOutput(
    val courts: List<CourtOutput>,
    val paginationInfo: PaginationInfo,
)

fun List<Court>.toCourtsOutput(paginationInfo: PaginationInfo) = CourtsOutput(this.map { CourtOutput(it) }, paginationInfo)
