package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Court

@Serializable
data class CourtsOutput(
    val courts: List<CourtDetailsOutput>,
)

fun List<Court>.toCourtsOutput() = CourtsOutput(this.map { CourtDetailsOutput(it) })
