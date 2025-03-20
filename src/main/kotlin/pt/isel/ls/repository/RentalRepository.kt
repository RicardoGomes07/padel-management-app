package pt.isel.ls.repository

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pt.isel.ls.domain.Rental

/**
 * Generic Interface for a Rental repository that supports CRUD operations.
 */
interface RentalRepository : Repository<Rental> {
    /**
     * Function that creates a new Rental.
     * rid is automatically incremented so it's not received as a parameter to the function.
     */
    fun createRental(
        date: LocalDate,
        rentalTime: IntRange,
        renterId: UInt,
        courtId: UInt,
    ): Rental

    /**
     * Finds all the available hours for a court in a given date.
     * @param crid The court id.
     * @param date The date to search for available hours.
     */
    fun findAvailableHoursForACourt(
        crid: UInt,
        date: LocalDateTime,
    ): List<LocalTime>

    /**
     * Finds all the rentals for a court in a given date.
     * @param crid The court id.
     * @param date The date to search for rentals if no date is received search for all the rentals of the court.
     */
    fun findByCridAndDate(
        crid: UInt,
        date: LocalDateTime?,
    ): List<Rental>

    /**
     * Finds all the rentals of a renter.
     * @param renter The renter id.
     */
    fun findAllRentalsByRenterId(renter: UInt): List<Rental>
}
