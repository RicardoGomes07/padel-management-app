package pt.isel.ls.repository

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pt.isel.ls.domain.Rental
import kotlin.time.Duration

/**
 * Generic Interface for a Rental repository that supports CRUD operations.
 */
interface RentalRepository : Repository<Rental> {
    /**
     * Function that creates a new Rental.
     * rid is automatically incremented so it's not received as a parameter to the function.
     */
    fun createRental(
        date: LocalDateTime,
        duration: Duration,
        renterId: UInt,
        courtId: UInt,
    ): Rental

    fun findAvailableHoursForACourt(
        crid: UInt,
        date: LocalDateTime,
    ): List<LocalTime>

    fun findByCridAndDate(
        crid: UInt,
        date: LocalDateTime?,
    ): List<Rental>

    fun findAllRentalsByRenterId(renter: UInt): List<Rental>
}
