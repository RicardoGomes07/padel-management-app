package pt.isel.ls.services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pt.isel.ls.domain.Rental
import pt.isel.ls.repository.RentalRepository

sealed class RentalError {
    data object RentalNotFound : RentalError()
}

class RentalService(
    private val rentalRepo: RentalRepository,
) {
    /**
     * Function that returns all rentals in of a court in a specific date
     * @param crid the court identifier
     * @param date the date
     * @return list of rentals
     */
    fun getRentals(
        crid: UInt,
        date: LocalDate?,
    ): List<Rental> = rentalRepo.findByCridAndDate(crid, date)

    /**
     * Function that returns a rental by its identifier
     * @param rid the rental identifier
     * @return either the rental or an error indicating that the rental was not found
     */
    fun getRentalById(rid: UInt): Either<RentalError.RentalNotFound, Rental> {
        val rental = rentalRepo.findByIdentifier(rid) ?: return failure(RentalError.RentalNotFound)
        return success(rental)
    }

    /**
     * Function that creates a new rental in the system
     * @param date the rental date
     * @param duration the rental duration
     * @param renterId the renter identifier
     * @param courtId the court identifier
     * @return the new rental
     */
    fun createRental(
        date: LocalDateTime,
        duration: LocalTime,
        renterId: UInt,
        courtId: UInt,
    ): Rental = rentalRepo.createRental(date, duration, renterId, courtId)

    /**
     * Get the available hours for a court in a specific date
     * @param crid the court identifier
     * @param date the date
     * @return either the available hours or an error indicating that there are no available hours
     */
    fun getAvailableHours(
        crid: UInt,
        date: LocalDateTime,
    ): List<LocalTime> {
        val hours = rentalRepo.findAvailableHoursForACourt(crid, date)
        return hours
    }

    /**
     * Function that returns all rentals that the user made
     * @param uid the user identifier
     * @return either the rentals or an error indicating that the user has no rentals
     */
    fun getUserRentals(uid: UInt): Either<RentalError.RentalNotFound, List<Rental>> {
        val rentals = rentalRepo.findAllRentalsByRenterId(uid) ?: return failure(RentalError.RentalNotFound)
        return success(rentals)
    }
}
