package pt.isel.ls.service

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Before
import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.repository.mem.ClubRepositoryInMem
import pt.isel.ls.repository.mem.CourtRepositoryInMem
import pt.isel.ls.repository.mem.RentalRepositoryInMem
import pt.isel.ls.repository.mem.UserRepositoryInMem
import pt.isel.ls.services.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours

class RentalServiceTests {
    private val rentalService = RentalService(RentalRepositoryInMem)
    private val clubService = ClubService(ClubRepositoryInMem)
    private val userService = UserService(UserRepositoryInMem)
    private val courtService = CourtService(CourtRepositoryInMem)


    @Before
    fun setUp() {
        RentalRepositoryInMem.clear()
        UserRepositoryInMem.clear()
        ClubRepositoryInMem.clear()
        CourtRepositoryInMem.clear()
    }

    private val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    @Test
    fun `create rental with valid renter and court`() {
        val renter = userService.createUser(Name("John Doe"), Email("john@example.com"))
        assertTrue(renter is Success)
        val club = clubService.createClub("Sports Club", renter.value)
        assertTrue(club is Success)
        val court = courtService.createCourt(Name("Court A"), club.value.cid)

        val rentalDate = Clock.System.now().plus(1.hours).toLocalDateTime(TimeZone.currentSystemDefault())
        val duration = 2.hours

        val rental = rentalService.createRental(rentalDate, duration, renter.value.uid, court.crid)

        assertEquals(rentalDate, rental.date)
        assertEquals(duration, rental.duration)
        assertEquals(renter.value, rental.renter)
        assertEquals(court, rental.court)
    }


}