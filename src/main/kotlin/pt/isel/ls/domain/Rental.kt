package pt.isel.ls.domain

import kotlinx.datetime.LocalDate

/**
 * Represents a rental.
 * @property rid Unique identifier for a rental.
 * @property date Date of the rental.
 * @property duration Duration of the rental.
 * @property renter User that is renting.
 * @property court Court that is being rented.
 */
data class Rental(
    val rid: UInt,
    val date: LocalDate,
    val rentTime: IntRange,
    val renter: User,
    val court: Court,
)
