@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository

import kotlinx.datetime.*
import org.junit.Assert.*
import org.junit.Before
import pt.isel.ls.repository.mem.*

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

    private val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    /*
    @Test
    fun `create rental with valid renter and court`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val rentalDate = Clock.System.now().plus(1.hours).toLocalDateTime(TimeZone.currentSystemDefault())
        val duration = 2.hours

        val rental = rentalRepo.createRental(rentalDate, duration, renter.uid, court.crid)

        assertEquals(rentalDate, rental.date)
        assertEquals(duration, rental.duration)
        assertEquals(renter, rental.renter)
        assertEquals(court, rental.court)
    }

    @Test
    fun `create rental with past date should fail`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val pastDate = Clock.System.now().minus(1.hours).toLocalDateTime(TimeZone.currentSystemDefault())

        assertFailsWith<IllegalArgumentException> {
            rentalRepo.createRental(pastDate, 2.hours, renter.uid, court.crid)
        }
    }

    @Test
    fun `create rental with non-existent court should fail`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))

        assertFailsWith<IllegalArgumentException> {
            rentalRepo.createRental(currentDateTime, 2.hours, renter.uid, 999u)
        }
    }

    @Test
    fun `find all rentals by renter id`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val rentalDate = Clock.System.now().plus(2.hours).toLocalDateTime(TimeZone.currentSystemDefault())

        rentalRepo.createRental(rentalDate, 1.hours, renter.uid, court.crid)
        rentalRepo.createRental(rentalDate.plusDuration(1.hours), 2.hours, renter.uid, court.crid)

        val rentals = rentalRepo.findAllRentalsByRenterId(renter.uid)
        assertEquals(2, rentals.size)
    }

    @Test
    fun `find rentals by court id and date`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val date = Clock.System.now().plus(2.hours).toLocalDateTime(TimeZone.currentSystemDefault())

        rentalRepo.createRental(date, 2.hours, renter.uid, court.crid)

        val foundRentals = rentalRepo.findByCridAndDate(court.crid, date)
        assertEquals(1, foundRentals.size)

        val allRentals = rentalRepo.findAll()
        assertEquals(1, allRentals.size)
    }

    @Test
    fun `find available hours for a court`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val date = Clock.System.now().plus(1.hours).toLocalDateTime(TimeZone.currentSystemDefault())

        rentalRepo.createRental(date, 2.hours, renter.uid, court.crid).also {
            println("Rental Start: ${it.date}")
            println("Rental Duration: ${it.duration}")
        }

        val availableHours = rentalRepo.findAvailableHoursForACourt(court.crid, date)

        println("Available hours:")
        availableHours.forEach {
            println(it.hour)
        }

        assertFalse(availableHours.contains(LocalTime(date.hour, 0)))
        assertFalse(availableHours.contains(LocalTime(date.hour + 1, 0)))
        assertTrue(availableHours.contains(LocalTime(date.hour + 3, 0)))
    }

    @Test
    fun `delete rental by id`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val rentalDate = currentDateTime.plusDuration(1.hours)

        val rental = rentalRepo.createRental(rentalDate, 2.hours, renter.uid, court.crid)

        rentalRepo.deleteByIdentifier(rental.rid)
        val foundRental = rentalRepo.findByIdentifier(rental.rid)
        assertNull(foundRental)
    }

    @Test
    fun `save updates an existing rental`() {
        val renter = userRepo.createUser(Name("John Doe"), Email("john@example.com"))
        val club = clubRepo.createClub("Sports Club", renter.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val rentalDate = currentDateTime.plusDuration(1.hours)

        val rental = rentalRepo.createRental(rentalDate, 2.hours, renter.uid, court.crid)

        val updatedRental = rental.copy(duration = 3.hours)
        rentalRepo.save(updatedRental)

        val retrievedRental = rentalRepo.findByIdentifier(rental.rid)
        assertEquals(3.hours, retrievedRental?.duration)
    }

     */
}
