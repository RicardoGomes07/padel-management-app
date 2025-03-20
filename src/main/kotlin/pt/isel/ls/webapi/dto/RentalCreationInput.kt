package pt.isel.ls.webapi.dto

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class RentalCreationInput(
    val cid: Int,
    val crid: Int,
    val date: LocalDateTime,
    val duration: Duration,
)
