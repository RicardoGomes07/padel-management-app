package pt.isel.ls.service

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.repository.mem.ClubRepositoryInMem
import pt.isel.ls.repository.mem.UserRepositoryInMem
import pt.isel.ls.services.ClubService
import pt.isel.ls.services.Failure
import pt.isel.ls.services.Success
import pt.isel.ls.services.UserService
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClubServiceTests {
    private val clubService = ClubService(ClubRepositoryInMem)
    private val userService = UserService(UserRepositoryInMem)

    @Before
    fun setUp() {
        ClubRepositoryInMem.clear()
        UserRepositoryInMem.clear()
    }

    @Test
    fun `create club with valid name and existing owner`() {
        val owner = userService.createUser(Name("owner"), Email("owner@email.com"))
        assertTrue(owner is Success)
        val club = clubService.createClub("Sporting", owner.value)
        assertTrue(club is Success)
        assertEquals(owner.value, club.value.owner)
    }

    @Test
    fun `create club with duplicate name should fail`() {
        val owner = userService.createUser(Name("Ric"), Email("ric@email.com"))
        assertTrue(owner is Success)
        val firstClub = clubService.createClub("Benfica", owner.value)
        assertTrue(firstClub is Success)
        val secondClub = clubService.createClub("Benfica", owner.value)
        assertTrue(secondClub is Failure)
    }

    @Test
    fun `find club by identifier`() {
        val owner = userService.createUser(Name("Ric1"), Email("ric@email.com"))
        assertTrue(owner is Success)
        val club = clubService.createClub("Porto", owner.value)
        assertTrue(club is Success)
        val foundClub = clubService.getClubById(club.value.cid)
        Assert.assertEquals(club, foundClub)
    }

    @Test
    fun `find all clubs`() {
        val owner = userService.createUser(Name("owner"), Email("owner@email.com"))
        assertTrue(owner is Success)
        val club1 = clubService.createClub("Braga", owner.value)
        assertTrue(club1 is Success)
        val club2 = clubService.createClub("Boavista", owner.value)
        assertTrue(club2 is Success)
        val allClubs = clubService.getClubs()
        assertEquals(2, allClubs.size)
    }
}
