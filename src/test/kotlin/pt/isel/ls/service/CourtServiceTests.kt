package pt.isel.ls.service

import org.junit.Before
import pt.isel.ls.repository.mem.ClubRepositoryInMem
import pt.isel.ls.repository.mem.CourtRepositoryInMem
import pt.isel.ls.repository.mem.UserRepositoryInMem
import pt.isel.ls.services.ClubService
import pt.isel.ls.services.CourtService
import pt.isel.ls.services.UserService

class CourtServiceTests {
    private val courtService = CourtService(CourtRepositoryInMem)
    private val clubService = ClubService(ClubRepositoryInMem)
    private val userService = UserService(UserRepositoryInMem)

    @Before
    fun setUp() {
        CourtRepositoryInMem.clear()
        ClubRepositoryInMem.clear()
        UserRepositoryInMem.clear()
    }
    /*
    @Test
    fun `create court with valid name and existing club`() {
        val owner = userService.createUser(Name("owner"), Email("owner@email.com"))
        assertTrue(owner is Success)
        val club = clubService.createClub("Sports Club", owner.value)
        assertTrue(club is Success)
        val court = courtService.createCourt(Name("Court A"), club.value.cid)

        assertEquals(Name("Court A"), court.name)
        assertEquals(club.value, court.club)
    }

    @Test
    fun `find courts by club identifier`() {
        val owner = userService.createUser(Name("owner"), Email("owner@email.com"))
        assertTrue(owner is Success)
        val club = clubService.createClub("Sports Club", owner.value)
        assertTrue(club is Success)
        val court1 = courtService.createCourt(Name("Court A"), club.value.cid)
        val court2 = courtService.createCourt(Name("Court B"), club.value.cid)

        val foundCourts = courtService.getCourts(club.value.cid)
        assertEquals(2, foundCourts.size)
        Assert.assertTrue(foundCourts.containsAll(listOf(court1, court2)))
    }

    @Test
    fun `find court by identifier`() {
        val owner = userService.createUser(Name("owner"), Email("owner@email.com"))
        assertTrue(owner is Success)
        val club = clubService.createClub("Sports Club", owner.value)
        assertTrue(club is Success)
        val court = courtService.createCourt(Name("Court A"), club.value.cid)

        val foundCourt = courtService.getCourtById(court.crid)
        assertEquals(court, foundCourt)
    }

    @Test
    fun `find all courts`() {
        val owner = userService.createUser(Name("owner"), Email("owner@email.com"))
        assertTrue(owner is Success)
        val club = clubService.createClub("Sports Club", owner.value)
        assertTrue(club is Success)
        val court1 = courtService.createCourt(Name("Court A"), club.value.cid)
        val court2 = courtService.createCourt(Name("Court B"), club.value.cid)

        val allCourts = courtService.getCourts(club.value.cid)
        assertEquals(2, allCourts.size)
        assertTrue(allCourts.containsAll(listOf(court1, court2)))
    }

     */
}
