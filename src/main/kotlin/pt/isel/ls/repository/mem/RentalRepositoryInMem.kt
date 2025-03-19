package pt.isel.ls.repository.mem

import kotlinx.datetime.*
import pt.isel.ls.domain.Rental
import pt.isel.ls.repository.RentalRepository
import pt.isel.ls.repository.mem.CourtRepositoryInMem.courts
import pt.isel.ls.repository.mem.UserRepositoryInMem.users
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object RentalRepositoryInMem : RentalRepository {
    private val rentals = mutableListOf<Rental>()

    private var currId = 0u

    /**
     * Creates a rental.
     * @param date The date of the rental.
     * @param duration The duration of the rental.
     * @param renterId The id of the renter.
     * @param courtId The id of the court.
     * @return The created rental.
     * @throws IllegalArgumentException If the rental date is in the past.
     * @throws IllegalArgumentException If the renter or court does not exist.
     */
    override fun createRental(
        date: LocalDateTime,
        duration: Duration,
        renterId: UInt,
        courtId: UInt,
    ): Rental {
        require(date >= Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) {
            "Rental date must be in the future"
        }
        currId += 1u

        val renter = users.firstOrNull { it.uid == renterId }

        requireNotNull(renter)

        val court = courts.firstOrNull { it.crid == courtId }

        requireNotNull(court)

        val rental =
            Rental(
                rid = currId,
                date = date,
                duration = duration,
                renter = renter,
                court = court,
            )

        rentals.add(rental)
        return rental
    }

    /**
     * Finds the available hours for a court on a given date.
     * @param crid The id of the court.
     * @param date The date to check.
     * @return The list available hours for the court on the given date.
     */
    override fun findAvailableHoursForACourt(
        crid: UInt,
        date: LocalDateTime,
    ): List<LocalTime> {
        // TODO: Fix the issue where available hours are only considered if they start on the hour.
        // Currently, the logic does not support rentals that begin at irregular times, such as 15:30
        // or 11:30.
        val court = courts.firstOrNull { it.crid == crid } ?: return emptyList()

        val rentalsOnCourtAtDay =
            rentals
                .filter { it.court == court && it.date.date == date.date }
                .map { rental ->
                    val rentalStart = rental.date.time
                    val rentalEnd = rental.date.plusDuration(rental.duration)
                    rentalStart to rentalEnd
                }

        return (0..23)
            .map { LocalTime(it, 0) }
            .filter { hour ->
                val hourEnd =
                    date.date
                        .atTime(hour)
                        .plusDuration(1.toDuration(DurationUnit.HOURS))
                        .time
                rentalsOnCourtAtDay.none { (rentalStart, rentalEnd) ->
                    hour < rentalEnd.time && hourEnd > rentalStart
                }
            }
    }

    /**
     * Finds all rentals for a court on a given date.
     * @param crid The id of the court.
     * @param date The date to check.
     * @return The list of rentals for the court on the given date.
     */
    override fun findByCridAndDate(
        crid: UInt,
        date: LocalDateTime?,
    ): List<Rental> =
        rentals.filter {
            it.court.crid == crid && it.date == date
        }

    /**
     * Finds all rentals by a renter id.
     * @param renter The id of the renter.
     * @return The list of rentals by the renter.
     */
    override fun findAllRentalsByRenterId(renter: UInt): List<Rental> =
        rentals.filter {
            it.renter.uid == renter
        }

    /**
     * Updates an existing rental or creates a new one if it's new.
     * @param element The rental to save.
     */
    override fun save(element: Rental) {
        rentals.removeIf { it.rid == element.rid }
        rentals.add(element)
    }

    /**
     * Finds a rental by its identifier.
     * @param id The identifier of the rental.
     * @return The rental with the given identifier or null if it does not exist.
     */
    override fun findByIdentifier(id: UInt): Rental? =
        rentals.firstOrNull {
            it.rid == id
        }

    /**
     * Finds all rentals.
     * @return The list of all rentals.
     */
    override fun findAll(): List<Rental> = rentals

    /**
     * Deletes a rental by its identifier.
     * @param id The identifier of the rental.
     */
    override fun deleteByIdentifier(id: UInt) {
        rentals.removeIf { it.rid == id }
    }

    /**
     * Clears all rentals.
     */
    override fun clear() {
        rentals.clear()
    }
}

/**
 * Adds a duration to a LocalDateTime.
 * @param duration The duration to add.
 * @param timeZone The time zone of the LocalDateTime.
 * @return The new LocalDateTime with the added duration.
 */
fun LocalDateTime.plusDuration(
    duration: Duration,
    timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    val instant = this.toInstant(timeZone)
    val newInstant = instant + duration
    return newInstant.toLocalDateTime(timeZone)
}
