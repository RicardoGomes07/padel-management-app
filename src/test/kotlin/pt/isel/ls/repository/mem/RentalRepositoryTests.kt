@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.mem

import kotlinx.datetime.*
import pt.isel.ls.domain.*
import pt.isel.ls.services.RentalError
import kotlin.test.*

class RentalRepositoryTests {
    private val rentalRepoInMem = RentalRepositoryInMem
    private val userRepoInMem = UserRepositoryInMem
    private val clubRepoInMem = ClubRepositoryInMem
    private val courtRepoInMem = CourtRepositoryInMem

    @BeforeTest
    fun setUp() {
        rentalRepoInMem.clear()
        userRepoInMem.clear()
        clubRepoInMem.clear()
        courtRepoInMem.clear()
    }

    private val tomorrowDate =
        Clock
            .System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .let { LocalDate(it.year, it.month, it.dayOfMonth).plus(1, DateTimeUnit.DAY) }

    @Test
    fun `create rental with valid renter and court`() {
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        val rentalDate = tomorrowDate
        val rentalTime = (10..12).toTimeSlot() // 2-hour rental from 10:00 to 12:00

        val rental = rentalRepoInMem.createRental(rentalDate, rentalTime, renter.uid, court.crid)

        assertEquals(rentalDate, rental.date)
        assertEquals(rentalTime, rental.rentTime)
        assertEquals(renter, rental.renter)
        assertEquals(court, rental.court)
    }

    @Test
    fun `create rental with past date should throw error RentalDateInPast`() {
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        val pastDate = tomorrowDate.minus(2, DateTimeUnit.DAY)

        assertFailsWith<RentalError.RentalDateInThePast> {
            rentalRepoInMem.createRental(pastDate, (10..12).toTimeSlot(), renter.uid, court.crid)
        }
    }

    @Test
    fun `overlapping should throw RentalAlreadyExists`() {
        val renter = userRepoInMem.createUser("Alice".toName(), "alice@email.com".toEmail())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        rentalRepoInMem.createRental(
            tomorrowDate,
            (10..12).toTimeSlot(),
            renter.uid,
            court.crid,
        )

        assertFailsWith<RentalError.RentalAlreadyExists> {
            rentalRepoInMem.createRental(
                tomorrowDate,
                (11..13).toTimeSlot(),
                renter.uid,
                court.crid,
            )
        }
    }

    @Test
    fun `find all rentals by renter id`() {
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        rentalRepoInMem.createRental(tomorrowDate, (9..10).toTimeSlot(), renter.uid, court.crid)
        rentalRepoInMem.createRental(tomorrowDate, (11..13).toTimeSlot(), renter.uid, court.crid)

        val rentals = rentalRepoInMem.findAllRentalsByRenterId(renter.uid)
        assertEquals(2, rentals.size)
    }

    @Test
    fun `find rentals by court id and date`() {
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        rentalRepoInMem
            .createRental(
                tomorrowDate,
                (14..16).toTimeSlot(),
                renter.uid,
                court.crid,
            )

        val foundRentals = rentalRepoInMem.findByCridAndDate(court.crid, null)
        assertEquals(1, foundRentals.size)
    }

    @Test
    fun `find available hours for a court`() {
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        rentalRepoInMem
            .createRental(
                tomorrowDate,
                (10..12).toTimeSlot(),
                renter.uid,
                court.crid,
            )

        val availableHours =
            rentalRepoInMem.findAvailableHoursForACourt(
                court.crid,
                tomorrowDate,
            )

        assertFalse(availableHours.contains(10.toUInt()))
        assertFalse(availableHours.contains(11.toUInt()))
        assertTrue(availableHours.contains(12.toUInt()))
    }

    @Test
    fun `delete rental by id`() {
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        val rental =
            rentalRepoInMem
                .createRental(
                    tomorrowDate,
                    (10..12).toTimeSlot(),
                    renter.uid,
                    court.crid,
                )

        rentalRepoInMem.deleteByIdentifier(rental.rid)
        val foundRental = rentalRepoInMem.findByIdentifier(rental.rid)
        assertNull(foundRental)
    }

    @Test
    fun `save updates an existing rental`() {
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        val rental =
            rentalRepoInMem
                .createRental(
                    tomorrowDate,
                    (10..12).toTimeSlot(),
                    renter.uid,
                    court.crid,
                )

        val updatedRental = rental.copy(rentTime = TimeSlot(12U, 15U))
        rentalRepoInMem.save(updatedRental)

        val retrievedRental =
            rentalRepoInMem
                .findAll()
                .firstOrNull {
                    it.date == updatedRental.date &&
                        it.rentTime == updatedRental.rentTime &&
                        it.renter == updatedRental.renter &&
                        it.court == updatedRental.court
                }
        assertEquals((12..15).toTimeSlot(), retrievedRental?.rentTime)
    }
}
