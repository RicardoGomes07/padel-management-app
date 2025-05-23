@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.service

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.TimeSlot
import pt.isel.ls.domain.toName
import pt.isel.ls.repository.mem.TransactionManagerInMem
import pt.isel.ls.services.*
import pt.isel.ls.webapi.currentDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days

class RentalServiceTests {
    private val transactionManager = TransactionManagerInMem()

    private val rentalService = RentalService(transactionManager)
    private val clubService = ClubService(transactionManager)
    private val userService = UserService(transactionManager)
    private val courtService = CourtService(transactionManager)

    @BeforeTest
    fun setUp() {
        transactionManager.run {
            it.rentalRepo.clear()
            it.courtRepo.clear()
            it.clubRepo.clear()
            it.userRepo.clear()
        }
    }

    @Test
    fun `create rental with valid renter and court`() {
        val renterResult = userService.createUser(Name("John Doe"), Email("john@example.com"))
        assertTrue(renterResult.isSuccess)
        val renter = renterResult.getOrNull()!!
        val club = clubService.createClub("Sports Club".toName(), renter)
        assertTrue(club.isSuccess)
        val courtResult = courtService.createCourt(Name("Court A"), club.getOrNull()!!.cid)
        assertTrue(courtResult.isSuccess)
        val court = courtResult.getOrNull()!!
        val rentalDate =
            Clock.System
                .now()
                .plus(1.days)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        val rentTime = TimeSlot(10u, 13u)

        val rentalResult = rentalService.createRental(rentalDate, rentTime, renter.uid, court.crid)
        assertTrue(rentalResult.isSuccess)
        val rental = rentalResult.getOrNull()!!

        assertEquals(rentalDate, rental.date)
        assertEquals(rentTime, rental.rentTime)
        assertEquals(renter, rental.renter)
        assertEquals(court, rental.court)
    }

    @Test
    fun `get available courts by rent and time should only return the courts available`() {
        val user =
            userService
                .createUser(
                    Name("John Doe"),
                    Email("jonh@doe.email.com"),
                ).let {
                    val user = it.getOrNull()
                    assertTrue(user != null)
                    user
                }
        val club =
            clubService
                .createClub(
                    "Sports Club".toName(),
                    user,
                ).let {
                    val club = it.getOrNull()
                    assertTrue(club != null)
                    club
                }
        val court1 =
            courtService
                .createCourt(
                    Name("Court A"),
                    club.cid,
                ).let {
                    val court = it.getOrNull()
                    assertTrue(court != null)
                    court
                }
        val court2 =
            courtService
                .createCourt(
                    Name("Court B"),
                    club.cid,
                ).let {
                    val court = it.getOrNull()
                    assertTrue(court != null)
                    court
                }

        val rental1 =
            rentalService.createRental(
                currentDate().plus(DatePeriod(days = 1)),
                TimeSlot(10u, 13u),
                user.uid,
                court1.crid,
            )

        val rental2 =
            rentalService.createRental(
                currentDate().plus(DatePeriod(days = 1)),
                TimeSlot(14u, 16u),
                user.uid,
                court2.crid,
            )

        val availableCourts =
            rentalService
                .getAvailableCourtsByDateAndRentTime(
                    club.cid,
                    currentDate().plus(DatePeriod(days = 1)),
                    TimeSlot(10u, 16u),
                ).let {
                    val availableCourts = it.getOrNull()
                    assertTrue(availableCourts != null)
                    availableCourts
                }

        assertEquals(0, availableCourts.count)

        val availableCourts1 =
            rentalService
                .getAvailableCourtsByDateAndRentTime(
                    club.cid,
                    currentDate().plus(DatePeriod(days = 1)),
                    TimeSlot(13u, 18u),
                ).let {
                    val availableCourts = it.getOrNull()
                    assertTrue(availableCourts != null)
                    availableCourts
                }

        assertEquals(1, availableCourts1.count)
    }
}
