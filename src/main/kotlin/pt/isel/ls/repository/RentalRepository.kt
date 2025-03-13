package pt.isel.ls.repository

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import pt.isel.ls.domain.Rental

/**
 * Generic Interface for a Rental repository that supports CRUD operations.
 */
interface RentalRepository : Repository<Rental> {
    /**
     * Function that creates a new Rental.
     * Returns the created element.
     * rid is automatically incremented so it's not received as a parameter to the function.
     */
    fun createRental(
        date: LocalDate,
        duration: LocalTime,
        renterId: UInt,
        courtId: UInt,
    ): Rental

    fun availableHoursForACourt(
        crid: UInt,
        date: LocalDate,
    ): List<LocalTime>?

    fun findByCridAndDate(
        crid: UInt,
        date: LocalDate,
    ): Rental?

    //
    fun findAllRentalsByRenterId(renter: UInt): List<Rental>?
}
