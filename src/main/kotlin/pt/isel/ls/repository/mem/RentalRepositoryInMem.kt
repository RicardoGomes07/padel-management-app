package pt.isel.ls.repository.mem

import pt.isel.ls.domain.Rental
import pt.isel.ls.repository.RentalRepository
import pt.isel.ls.repository.mem.CourtRepositoryInMem.courts
import pt.isel.ls.repository.mem.UserRepositoryInMem.users
import java.time.LocalDateTime
import java.time.LocalTime

object RentalRepositoryInMem : RentalRepository {
    val rentals = mutableListOf<Rental>()

    private var currId = 0u

    override fun createRental(
        date: LocalDateTime,
        duration: LocalTime,
        renterId: UInt,
        courtId: UInt,
    ) {
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
    }

    override fun findAvailableHoursForACourt(
        crid: UInt,
        date: LocalDateTime,
    ): List<LocalTime> {
        val court = courts.firstOrNull { it.crid == crid }

        requireNotNull(court)

        val rentalsOnCourtAtDay =
            rentals
                .filter {
                    it.court == court && it.date.dayOfYear == date.dayOfYear
                }.sortedBy { it.date }

        val availableHours = mutableListOf<LocalTime>()

        val day = date.toLocalDate()

        val startOfDay = day.atStartOfDay()
        val endOfDay = startOfDay.plusDays(1).toLocalTime()

        var currentHour = startOfDay.toLocalTime()

        while (currentHour.isBefore(endOfDay)) {
            val hourStart = currentHour.atDate(day)
            val hourEnd = hourStart.plusHours(1)

            var isAvailable = true

            for ((_, rDate, rDuration, _, _) in rentalsOnCourtAtDay) {
                val rentalEnd = rDate.plusSeconds(rDuration.toSecondOfDay().toLong())

                // check if hour overlaps with rental
                if (!hourEnd.isBefore(rDate) || hourStart.isAfter(rentalEnd)) {
                    isAvailable = false
                    break
                }
            }

            if (isAvailable) {
                availableHours.add(currentHour)
            }

            currentHour = currentHour.plusHours(1)
        }

        return availableHours
    }

    override fun findByCridAndDate(
        crid: UInt,
        date: LocalDateTime,
    ): Rental? =
        rentals.firstOrNull {
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
