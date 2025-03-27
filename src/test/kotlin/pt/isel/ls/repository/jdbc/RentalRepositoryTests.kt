@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import kotlinx.datetime.*
import pt.isel.ls.domain.TimeSlot
import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.domain.toTimeSlot
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*

class RentalRepositoryTests {
    private val connection: Connection = DriverManager.getConnection(DB_URL)

    private val rentalRepoJdbc = RentalRepositoryJdbc(connection)
    private val userRepoJdbc = UserRepositoryJdbc(connection)
    private val clubRepoJdbc = ClubRepositoryJdbc(connection)
    private val courtRepoJdbc = CourtRepositoryJdbc(connection)

    @BeforeTest
    fun setUp() {
        rentalRepoJdbc.clear()
        userRepoJdbc.clear()
        clubRepoJdbc.clear()
        courtRepoJdbc.clear()
    }

    private val tomorrowDate =
        Clock
            .System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .let { LocalDate(it.year, it.month, it.dayOfMonth).plus(1, DateTimeUnit.DAY) }

    @Test
    fun `create rental with valid renter and court`() {
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        val rentalDate = tomorrowDate
        val rentalTime = (10..12).toTimeSlot() // 2-hour rental from 10:00 to 12:00

        val rental = rentalRepoJdbc.createRental(rentalDate, rentalTime, renter.uid, court.crid)

        assertEquals(rentalDate, rental.date)
        assertEquals(rentalTime, rental.rentTime)
        assertEquals(renter, rental.renter)
        assertEquals(court, rental.court)
    }

    @Test
    fun `create rental with past date should fail`() {
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        val pastDate = tomorrowDate.minus(2, DateTimeUnit.DAY)

        assertFailsWith<IllegalArgumentException> {
            rentalRepoJdbc.createRental(pastDate, (10..12).toTimeSlot(), renter.uid, court.crid)
        }
    }

    @Test
    fun `find all rentals by renter id`() {
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        rentalRepoJdbc.createRental(tomorrowDate, (9..10).toTimeSlot(), renter.uid, court.crid)
        rentalRepoJdbc.createRental(tomorrowDate, (11..13).toTimeSlot(), renter.uid, court.crid)

        val rentals = rentalRepoJdbc.findAllRentalsByRenterId(renter.uid)
        assertEquals(2, rentals.size)
    }

    @Test
    fun `find rentals by court id and date`() {
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        rentalRepoJdbc
            .createRental(
                tomorrowDate,
                (14..16).toTimeSlot(),
                renter.uid,
                court.crid,
            )

        val foundRentals = rentalRepoJdbc.findByCridAndDate(court.crid, null)
        assertEquals(1, foundRentals.size)
    }

    @Test
    fun `find available hours for a court`() {
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        rentalRepoJdbc
            .createRental(
                tomorrowDate,
                (10..12).toTimeSlot(),
                renter.uid,
                court.crid,
            )

        val availableHours =
            rentalRepoJdbc.findAvailableHoursForACourt(
                court.crid,
                tomorrowDate,
            )

        assertFalse(availableHours.contains(10.toUInt()))
        assertFalse(availableHours.contains(11.toUInt()))
        assertTrue(availableHours.contains(12.toUInt()))
    }

    @Test
    fun `delete rental by id`() {
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        val rental =
            rentalRepoJdbc
                .createRental(
                    tomorrowDate,
                    (10..12).toTimeSlot(),
                    renter.uid,
                    court.crid,
                )

        rentalRepoJdbc.deleteByIdentifier(rental.rid)
        val foundRental = rentalRepoJdbc.findByIdentifier(rental.rid)
        assertNull(foundRental)
    }

    @Test
    fun `save updates an existing rental`() {
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        val rental =
            rentalRepoJdbc
                .createRental(
                    tomorrowDate,
                    (10..12).toTimeSlot(),
                    renter.uid,
                    court.crid,
                )

        val updatedRental = rental.copy(rentTime = TimeSlot(12U, 15U))
        rentalRepoJdbc.save(updatedRental)

        val retrievedRental =
            rentalRepoJdbc
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
