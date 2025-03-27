package pt.isel.ls.service

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.repository.mem.ClubRepositoryInMem
import pt.isel.ls.repository.mem.UserRepositoryInMem
import pt.isel.ls.services.ClubService
import pt.isel.ls.services.UserService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClubServiceTests {
    private val clubService = ClubService(ClubRepositoryInMem)
    private val userService = UserService(UserRepositoryInMem)

    @BeforeTest
    fun setUp() {
        ClubRepositoryInMem.clear()
        UserRepositoryInMem.clear()
    }

    @Test
    fun `create club with valid name and existing owner`() {
        val ownerResult = userService.createUser(Name("owner"), Email("owner@email.com"))
        assertTrue(ownerResult.isSuccess)
        val clubResult = clubService.createClub("Sporting".toName(), ownerResult.getOrNull()!!)
        assertTrue(clubResult.isSuccess)
        assertEquals(ownerResult.getOrNull(), clubResult.getOrNull()!!.owner)
    }

    @Test
    fun `create club with duplicate name should fail`() {
        val ownerResult = userService.createUser(Name("Ric"), Email("ric@email.com"))
        assertTrue(ownerResult.isSuccess)
        val owner = ownerResult.getOrNull()!!
        val firstClubResult = clubService.createClub("Benfica".toName(), owner)
        assertTrue(firstClubResult.isSuccess)
        val secondClubResult = clubService.createClub("Benfica".toName(), owner)
        assertTrue(secondClubResult.isFailure)
    }

    @Test
    fun `find club by identifier`() {
        val owner = userService.createUser(Name("Ric1"), Email("ric@email.com"))
        assertTrue(owner.isSuccess)
        val club = clubService.createClub("Porto".toName(), owner.getOrNull()!!)
        assertTrue(club.isSuccess)
        val foundClub = clubService.getClubById(club.getOrNull()!!.cid)
        assertEquals(club, foundClub)
    }

    @Test
    fun `find all clubs`() {
        val ownerResult = userService.createUser("owner".toName(), "owner@email.com".toEmail())
        assertTrue(ownerResult.isSuccess)
        val owner = ownerResult.getOrNull()!!
        val club1 = clubService.createClub("Braga".toName(), owner)
        assertTrue(club1.isSuccess)
        val club2 = clubService.createClub("Boavista".toName(), owner)
        assertTrue(club2.isSuccess)
        val allClubs = clubService.getClubs(30, 0)
        assertEquals(2, allClubs.getOrNull()?.size)
    }
}
