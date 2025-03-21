@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository

import kotlinx.datetime.*
import org.junit.Assert.*
import org.junit.Before
import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.TimeSlot
import pt.isel.ls.repository.mem.*
import kotlin.test.Test
import kotlin.test.assertFailsWith

class RentalRepositoryTests {
    private val rentalRepo = RentalRepositoryInMem
    private val userRepo = UserRepositoryInMem
    private val clubRepo = ClubRepositoryInMem
    private val courtRepo = CourtRepositoryInMem

    @Before
    fun setUp() {
        rentalRepo.clear()
        userRepo.clear()
        clubRepo.clear()
        courtRepo.clear()
    }

    private val tomorrowDate =
        Clock
            .System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .let { LocalDate(it.year, it.month, it.dayOfMonth).plus(1, DateTimeUnit.DAY) }

    @Test
    fun `create rental with valid renter and court`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val rentalDate = tomorrowDate
        val rentalTime = 10..12 // 2-hour rental from 10:00 to 12:00

        val rental = rentalRepo.createRental(rentalDate, rentalTime, renter.uid, court.crid)

        assertEquals(rentalDate, rental.date)
        assertEquals(rentalTime, rental.rentTime)
        assertEquals(renter, rental.renter)
        assertEquals(court, rental.court)
    }

    @Test
    fun `create rental with past date should fail`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val pastDate = tomorrowDate.minus(2, DateTimeUnit.DAY)

        assertFailsWith<IllegalArgumentException> {
            rentalRepo.createRental(pastDate, 10..12, renter.uid, court.crid)
        }
    }

    @Test
    fun `find all rentals by renter id`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        rentalRepo.createRental(tomorrowDate, 9..10, renter.uid, court.crid)
        rentalRepo.createRental(tomorrowDate, 11..13, renter.uid, court.crid)

        val rentals = rentalRepo.findAllRentalsByRenterId(renter.uid)
        assertEquals(2, rentals.size)
    }

    @Test
    fun `find rentals by court id and date`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        rentalRepo.createRental(tomorrowDate, 14..16, renter.uid, court.crid)

        val foundRentals = rentalRepo.findByCridAndDate(court.crid, null)
        assertEquals(1, foundRentals.size)
    }

    @Test
    fun `find available hours for a court`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        rentalRepo.createRental(tomorrowDate, 10..12, renter.uid, court.crid)

        val availableHours =
            rentalRepo.findAvailableHoursForACourt(
                court.crid,
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            )

        assertFalse(availableHours.contains(LocalTime(10, 0)))
        assertFalse(availableHours.contains(LocalTime(11, 0)))
        assertTrue(availableHours.contains(LocalTime(12, 0)))
    }

    @Test
    fun `delete rental by id`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val rental = rentalRepo.createRental(tomorrowDate, 10..12, renter.uid, court.crid)

        rentalRepo.deleteByIdentifier(rental.rid)
        val foundRental = rentalRepo.findByIdentifier(rental.rid)
        assertNull(foundRental)
    }

    @Test
    fun `save updates an existing rental`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val rental = rentalRepo.createRental(tomorrowDate, 10..12, renter.uid, court.crid)

        val updatedRental = rental.copy(rentTime = TimeSlot(12U, 15U))
        rentalRepo.save(updatedRental)

        val retrievedRental = rentalRepo.findByIdentifier(rental.rid)
        assertEquals(12..15, retrievedRental?.rentTime)
    }
}
