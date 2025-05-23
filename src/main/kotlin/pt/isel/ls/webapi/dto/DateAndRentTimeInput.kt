package pt.isel.ls.webapi.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class DateAndRentTimeInput(
    val date: LocalDate,
    val initialHour: UInt,
    val finalHour: UInt,
)
