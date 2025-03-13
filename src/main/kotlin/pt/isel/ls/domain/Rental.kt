package pt.isel.ls.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Represents a rental.
 * @property rid Unique identifier for a rental.
 * @property date Date of the rental.
 * @property duration Duration of the rental in hours.
 * @property renter User that is renting.
 * @property court Court that is being rented.
 */
data class Rental(
    val rid: UInt,
    val date: LocalDate,
    val duration: LocalTime,
    val renter: User,
    val court: Court,
)
