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
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        val rentalDate = tomorrowDate
        val rentalTime = TimeSlot(10u, 12u) // 2-hour rental from 10:00 to 12:00

        val rental = rentalRepoInMem.createRental(rentalDate, rentalTime, renter.uid, court.crid)

        assertEquals(rentalDate, rental.date)
        assertEquals(rentalTime, rental.rentTime)
        assertEquals(renter, rental.renter)
        assertEquals(court, rental.court)
    }

    @Test
    fun `create rental with past date should throw error RentalDateInPast`() {
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        val pastDate = tomorrowDate.minus(2, DateTimeUnit.DAY)

        assertFailsWith<RentalError.RentalDateInThePast> {
            rentalRepoInMem.createRental(pastDate, TimeSlot(10u, 12u), renter.uid, court.crid)
        }
    }

    @Test
    fun `overlapping should throw RentalAlreadyExists`() {
        val renter = userRepoInMem.createUser("Alice".toName(), "alice@email.com".toEmail(), "password".toPassword())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        rentalRepoInMem.createRental(
            tomorrowDate,
            TimeSlot(10u, 12u),
            renter.uid,
            court.crid,
        )

        assertFailsWith<RentalError.OverlapInTimeSlot> {
            rentalRepoInMem.createRental(
                tomorrowDate,
                TimeSlot(11u, 13u),
                renter.uid,
                court.crid,
            )
        }
    }

    @Test
    fun `find all rentals by renter id`() {
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        rentalRepoInMem.createRental(tomorrowDate, TimeSlot(9u, 10u), renter.uid, court.crid)
        rentalRepoInMem.createRental(tomorrowDate, TimeSlot(11u, 13u), renter.uid, court.crid)

        val numOfRentals = rentalRepoInMem.numRentalsOfUser(renter.uid)
        assertEquals(2, numOfRentals)
    }

    @Test
    fun `find rentals by court id and date`() {
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        rentalRepoInMem
            .createRental(
                tomorrowDate,
                TimeSlot(14u, 16u),
                renter.uid,
                court.crid,
            )

        val numOfRentals = rentalRepoInMem.numRentalsOfCourt(court.crid, null)
        assertEquals(1, numOfRentals)
    }

    @Test
    fun `find available hours for a court`() {
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        rentalRepoInMem
            .createRental(
                tomorrowDate,
                TimeSlot(10u, 12u),
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
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        val rental =
            rentalRepoInMem
                .createRental(
                    tomorrowDate,
                    TimeSlot(10u, 12u),
                    renter.uid,
                    court.crid,
                )

        rentalRepoInMem.deleteByIdentifier(rental.rid)
        val foundRental = rentalRepoInMem.findByIdentifier(rental.rid)
        assertNull(foundRental)
    }

    @Test
    fun `save updates an existing rental`() {
        val renter = userRepoInMem.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoInMem.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        val rental =
            rentalRepoInMem
                .createRental(
                    tomorrowDate,
                    TimeSlot(10u, 12u),
                    renter.uid,
                    court.crid,
                )

        val updatedRental = rental.copy(rentTime = TimeSlot(12U, 15U))
        rentalRepoInMem.save(updatedRental)

        val retrievedRental =
            rentalRepoInMem
                .findAll()
                .items
                .firstOrNull {
                    it.date == updatedRental.date &&
                        it.rentTime == updatedRental.rentTime &&
                        it.renter == updatedRental.renter &&
                        it.court == updatedRental.court
                }
        assertEquals(TimeSlot(12u, 15u), retrievedRental?.rentTime)
    }
}
