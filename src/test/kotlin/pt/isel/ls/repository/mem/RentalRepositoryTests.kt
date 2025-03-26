@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.mem

import kotlinx.datetime.*
import pt.isel.ls.domain.TimeSlot
import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.domain.toTimeSlot
import pt.isel.ls.repository.jdbc.*
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
)

class RentalRepositoryTests {
    private val rentalRepoInMem = RentalRepositoryInMem
    private val userRepoInMem = UserRepositoryInMem
    private val clubRepoInMem = ClubRepositoryInMem
    private val courtRepoInMem = CourtRepositoryInMem

    private val connection: Connection = DriverManager.getConnection(DB_URL)

    private val rentalRepoJdbc = RentalRepositoryJdbc(connection)
    private val userRepoJdbc = UserRepositoryJdbc(connection)
    private val clubRepoJdbc = ClubRepositoryJdbc(connection)
    private val courtRepoJdbc = CourtRepositoryJdbc(connection)

    private val implementations =
        listOf(
            Quadruple(rentalRepoInMem, userRepoInMem, clubRepoInMem, courtRepoInMem),
            Quadruple(rentalRepoJdbc, userRepoJdbc, clubRepoJdbc, courtRepoJdbc),
        )

    @BeforeTest
    fun setUp() {
        implementations.forEach { (rentalRepo, userRepo, clubRepo, courtRepo) ->
            rentalRepo.clear()
            userRepo.clear()
            clubRepo.clear()
            courtRepo.clear()
        }
    }

    private val tomorrowDate =
        Clock
            .System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .let { LocalDate(it.year, it.month, it.dayOfMonth).plus(1, DateTimeUnit.DAY) }

    @Test
    fun `create rental with valid renter and court`() {
        implementations.forEach { (rentalRepo, userRepo, clubRepo, courtRepo) ->
            val renter = userRepo.createUser("John Doe".toName(), "john@example.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), renter.uid)
            val court = courtRepo.createCourt("Court A".toName(), club.cid)

            val rentalDate = tomorrowDate
            val rentalTime = (10..12).toTimeSlot() // 2-hour rental from 10:00 to 12:00

            val rental = rentalRepo.createRental(rentalDate, rentalTime, renter.uid, court.crid)

            assertEquals(rentalDate, rental.date)
            assertEquals(rentalTime, rental.rentTime)
            assertEquals(renter, rental.renter)
            assertEquals(court, rental.court)
        }
    }

    @Test
    fun `create rental with past date should fail`() {
        implementations.forEach { (rentalRepo, userRepo, clubRepo, courtRepo) ->
            val renter = userRepo.createUser("John Doe".toName(), "john@example.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), renter.uid)
            val court = courtRepo.createCourt("Court A".toName(), club.cid)

            val pastDate = tomorrowDate.minus(2, DateTimeUnit.DAY)

            assertFailsWith<IllegalArgumentException> {
                rentalRepo.createRental(pastDate, (10..12).toTimeSlot(), renter.uid, court.crid)
            }
        }
    }

    @Test
    fun `find all rentals by renter id`() {
        implementations.forEach { (rentalRepo, userRepo, clubRepo, courtRepo) ->
            val renter = userRepo.createUser("John Doe".toName(), "john@example.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), renter.uid)
            val court = courtRepo.createCourt("Court A".toName(), club.cid)

            rentalRepo.createRental(tomorrowDate, (9..10).toTimeSlot(), renter.uid, court.crid)
            rentalRepo.createRental(tomorrowDate, (11..13).toTimeSlot(), renter.uid, court.crid)

            val rentals = rentalRepo.findAllRentalsByRenterId(renter.uid)
            assertEquals(2, rentals.size)
        }
    }

    @Test
    fun `find rentals by court id and date`() {
        implementations.forEach { (rentalRepo, userRepo, clubRepo, courtRepo) ->
            val renter = userRepo.createUser("John Doe".toName(), "john@example.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), renter.uid)
            val court = courtRepo.createCourt("Court A".toName(), club.cid)

            rentalRepo
                .createRental(
                    tomorrowDate,
                    (14..16).toTimeSlot(),
                    renter.uid,
                    court.crid,
                )

            val foundRentals = rentalRepo.findByCridAndDate(court.crid, null)
            assertEquals(1, foundRentals.size)
        }
    }

    @Test
    fun `find available hours for a court`() {
        implementations.forEach { (rentalRepo, userRepo, clubRepo, courtRepo) ->
            val renter = userRepo.createUser("John Doe".toName(), "john@example.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), renter.uid)
            val court = courtRepo.createCourt("Court A".toName(), club.cid)

            rentalRepo
                .createRental(
                    tomorrowDate,
                    (10..12).toTimeSlot(),
                    renter.uid,
                    court.crid,
                )

            val availableHours =
                rentalRepo.findAvailableHoursForACourt(
                    court.crid,
                    tomorrowDate,
                )

            assertFalse(availableHours.contains(10.toUInt()))
            assertFalse(availableHours.contains(11.toUInt()))
            assertTrue(availableHours.contains(12.toUInt()))
        }
    }

    @Test
    fun `delete rental by id`() {
        implementations.forEach { (rentalRepo, userRepo, clubRepo, courtRepo) ->
            val renter = userRepo.createUser("John Doe".toName(), "john@example.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), renter.uid)
            val court = courtRepo.createCourt("Court A".toName(), club.cid)

            val rental =
                rentalRepo
                    .createRental(
                        tomorrowDate,
                        (10..12).toTimeSlot(),
                        renter.uid,
                        court.crid,
                    )

            rentalRepo.deleteByIdentifier(rental.rid)
            val foundRental = rentalRepo.findByIdentifier(rental.rid)
            assertNull(foundRental)
        }
    }

    @Test
    fun `save updates an existing rental`() {
        implementations.forEach { (rentalRepo, userRepo, clubRepo, courtRepo) ->
            val renter = userRepo.createUser("John Doe".toName(), "john@example.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), renter.uid)
            val court = courtRepo.createCourt("Court A".toName(), club.cid)

            val rental =
                rentalRepo
                    .createRental(
                        tomorrowDate,
                        (10..12).toTimeSlot(),
                        renter.uid,
                        court.crid,
                    )

            val updatedRental = rental.copy(rentTime = TimeSlot(12U, 15U))
            rentalRepo.save(updatedRental)

            val retrievedRental =
                rentalRepo
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
}
