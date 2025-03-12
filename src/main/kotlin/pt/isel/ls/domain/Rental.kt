package pt.isel.ls.domain

import java.util.Date

/**
 * Represents a rental.
 * @property rid Unique identifier for a rental.
 * @property date Date of the rental.
 * @property duration Duration of the rental in hours.
 * @property renter User that is renting.
 * @property court Court that is being rented.
 */
data class Rental(
    val rid: Int,
    val date: Date,
    val duration: UInt,
    val renter: User,
    val court: Court,
)
