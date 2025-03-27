@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.service

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.TimeSlot
import pt.isel.ls.domain.toName
import pt.isel.ls.repository.mem.ClubRepositoryInMem
import pt.isel.ls.repository.mem.CourtRepositoryInMem
import pt.isel.ls.repository.mem.RentalRepositoryInMem
import pt.isel.ls.repository.mem.UserRepositoryInMem
import pt.isel.ls.services.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days

class RentalServiceTests {
    private val rentalService = RentalService(RentalRepositoryInMem)
    private val clubService = ClubService(ClubRepositoryInMem)
    private val userService = UserService(UserRepositoryInMem)
    private val courtService = CourtService(CourtRepositoryInMem)

    @BeforeTest
    fun setUp() {
        RentalRepositoryInMem.clear()
        UserRepositoryInMem.clear()
        ClubRepositoryInMem.clear()
        CourtRepositoryInMem.clear()
    }

    private val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

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
        val rentalDate = Clock.System.now().plus(1.days).toLocalDateTime(TimeZone.currentSystemDefault()).date
        val rentTime = TimeSlot(10u, 13u)

        val rentalResult = rentalService.createRental(rentalDate, rentTime, renter.uid, court.crid)
        assertTrue(rentalResult.isSuccess)
        val rental = rentalResult.getOrNull()!!

        assertEquals(rentalDate, rental.date)
        assertEquals(rentTime, rental.rentTime)
        assertEquals(renter, rental.renter)
        assertEquals(court, rental.court)
    }
}
