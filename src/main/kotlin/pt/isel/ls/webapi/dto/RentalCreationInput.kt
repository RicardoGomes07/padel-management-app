package pt.isel.ls.webApi.Dto

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class RentalCreationInput(
    val cid: Int,
    val crid: Int,
    val date: LocalDate,
    val duration: LocalTime,
)
