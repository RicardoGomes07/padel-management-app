package pt.isel.ls.repository.mem

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
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

    override fun createRental(
        date: LocalDateTime,
        duration: Duration,
        renterId: UInt,
        courtId: UInt,
    ): Rental {
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

    override fun findAvailableHoursForACourt(
        crid: UInt,
        date: LocalDateTime,
    ): List<LocalTime> {
        val court = courts.firstOrNull { it.crid == crid } ?: return emptyList()

        val rentalsOnCourtAtDay =
            rentals
                .filter { it.court == court && it.date.date == date.date }
                .map { rental ->
                    val rentalStart = rental.date.time
                    val rentalEnd = rental.date + rental.duration
                    rentalStart to rentalEnd
                }

        return (0..23)
            .map { LocalTime(it, 0) }
            .filter { hour ->
                // Combine hour with the same date and add 1 hour to it
                val hourEnd =
                    date.date
                        .atTime(hour)
                        .plus(1.toDuration(DurationUnit.HOURS))
                        .time
                rentalsOnCourtAtDay.none { (rentalStart, rentalEnd) ->
                    hour < rentalEnd.time && hourEnd > rentalStart
                }
            }
    }

    private operator fun LocalDateTime.plus(duration: Duration): LocalDateTime = this.plus(duration)

    override fun findByCridAndDate(
        crid: UInt,
        date: LocalDateTime?,
    ): List<Rental> =
        rentals.filter {
            it.court.crid == crid && it.date == date
        }

    override fun findAllRentalsByRenterId(renter: UInt): List<Rental> =
        rentals.filter {
            it.renter.uid == renter
        }

    override fun save(element: Rental) {
        val findRental = rentals.find { it.rid == element.rid }

        // rental exists, so update
        if (findRental != null) {
            rentals.map { rental ->
                if (rental.rid == element.rid) {
                    element
                } else {
                    rental
                }
            }
        } else {
            // add element to the rental list
            rentals.add(element)
        }
    }

    override fun findByIdentifier(id: UInt): Rental? =
        rentals.firstOrNull {
            it.rid == id
        }

    override fun findAll(): List<Rental> = rentals

    override fun deleteByIdentifier(id: UInt) {
        rentals.removeIf { it.rid == id }
    }
}
