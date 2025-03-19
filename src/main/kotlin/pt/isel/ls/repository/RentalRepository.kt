package pt.isel.ls.repository

import pt.isel.ls.domain.Rental
import java.time.LocalDateTime
import java.time.LocalTime

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
        duration: LocalTime,
        renterId: UInt,
        courtId: UInt,
    )

    fun findAvailableHoursForACourt(
        crid: UInt,
        date: LocalDateTime,
    ): List<LocalTime>

    fun findByCridAndDate(
        crid: UInt,
        date: LocalDateTime,
    ): Rental?

    fun findAllRentalsByRenterId(renter: UInt): List<Rental>
}
