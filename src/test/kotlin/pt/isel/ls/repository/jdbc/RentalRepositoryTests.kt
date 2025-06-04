@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import kotlinx.datetime.*
import pt.isel.ls.domain.TimeSlot
import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.domain.toPassword
import pt.isel.ls.services.RentalError
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*

class RentalRepositoryTests {
    private val connection: Connection =
        DriverManager
            .getConnection(
                System.getenv("DB_URL")
                    ?: throw Exception("Missing DB_URL environment variable"),
            )

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
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        val rentalDate = tomorrowDate
        val rentalTime = TimeSlot(10u, 12u) // 2-hour rental from 10:00 to 12:00

        val rental = rentalRepoJdbc.createRental(rentalDate, rentalTime, renter.uid, court.crid)

        assertEquals(rentalDate, rental.date)
        assertEquals(rentalTime, rental.rentTime)
        assertEquals(renter, rental.renter)
        assertEquals(court, rental.court)
    }

    @Test
    fun `create rental with past date should throw error RentalDateInPast`() {
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        val pastDate = tomorrowDate.minus(2, DateTimeUnit.DAY)

        val currTime =
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())

        assertFailsWith<RentalError.RentalDateInThePast> {
            rentalRepoJdbc.createRental(pastDate, TimeSlot(10u, 12u), renter.uid, court.crid)
        }
        assertFailsWith<RentalError.RentalDateInThePast> {
            rentalRepoJdbc.createRental(
                currTime.date,
                TimeSlot(
                    (currTime.hour - 1).toUInt(),
                    currTime.hour.toUInt(),
                ),
                renter.uid,
                court.crid,
            )
        }
    }

    @Test
    fun `overlapping should throw RentalAlreadyExists`() {
        val renter = userRepoJdbc.createUser("Alice".toName(), "alice@email.com".toEmail(), "password".toPassword())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        rentalRepoJdbc.createRental(
            tomorrowDate,
            TimeSlot(10u, 12u),
            renter.uid,
            court.crid,
        )

        assertFailsWith<RentalError.OverlapInTimeSlot> {
            rentalRepoJdbc.createRental(
                tomorrowDate,
                TimeSlot(11u, 13u),
                renter.uid,
                court.crid,
            )
        }
    }

    @Test
    fun `find all rentals by renter id`() {
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        rentalRepoJdbc.createRental(tomorrowDate, TimeSlot(9u, 10u), renter.uid, court.crid)
        rentalRepoJdbc.createRental(tomorrowDate, TimeSlot(11u, 13u), renter.uid, court.crid)

        val numOfRentals = rentalRepoJdbc.numRentalsOfUser(renter.uid)
        assertEquals(2, numOfRentals)
    }

    @Test
    fun `find rentals by court id and date`() {
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)
        val court2 = courtRepoJdbc.createCourt("Court B".toName(), club.cid)

        rentalRepoJdbc
            .createRental(
                tomorrowDate,
                TimeSlot(14u, 16u),
                renter.uid,
                court.crid,
            )

        rentalRepoJdbc
            .createRental(
                tomorrowDate.plus(1, DateTimeUnit.DAY),
                TimeSlot(17u, 20u),
                renter.uid,
                court.crid,
            )

        val foundRentals = rentalRepoJdbc.findByCridAndDate(court.crid, null)
        assertEquals(2, foundRentals.count)

        val numOfTomorrowRentals = rentalRepoJdbc.numRentalsOfCourt(court.crid, tomorrowDate)
        assertEquals(1, numOfTomorrowRentals)

        val numOfOtherCourtRentals = rentalRepoJdbc.numRentalsOfCourt(court2.crid, tomorrowDate)
        assertEquals(0, numOfOtherCourtRentals)
    }

    @Test
    fun `find available hours for a court`() {
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        rentalRepoJdbc
            .createRental(
                tomorrowDate,
                TimeSlot(10u, 12u),
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
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        val rental =
            rentalRepoJdbc
                .createRental(
                    tomorrowDate,
                    TimeSlot(10u, 12u),
                    renter.uid,
                    court.crid,
                )

        assertEquals(rental, rentalRepoJdbc.findByIdentifier(rental.rid))

        rentalRepoJdbc.deleteByIdentifier(rental.rid)
        val foundRental = rentalRepoJdbc.findByIdentifier(rental.rid)
        assertNull(foundRental)
    }

    @Test
    fun `save updates an existing rental`() {
        val renter = userRepoJdbc.createUser("John Doe".toName(), "john@example.com".toEmail(), "password".toPassword())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), renter.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        val rental =
            rentalRepoJdbc
                .createRental(
                    tomorrowDate,
                    TimeSlot(10u, 12u),
                    renter.uid,
                    court.crid,
                )

        val updatedRental = rental.copy(rentTime = TimeSlot(12U, 15U))
        rentalRepoJdbc.save(updatedRental)

        val retrievedRental =
            rentalRepoJdbc
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
