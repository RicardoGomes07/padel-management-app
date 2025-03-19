package pt.isel.ls.webApi.Dto

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class RentalDetailsOutput(
    val club: ClubDetailsOutput,
    val court: CourtDetailsOutput,
    val date: LocalDate,
    val duration: LocalTime,
)
