package pt.isel.ls.service

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.domain.toPassword
import pt.isel.ls.repository.mem.TransactionManagerInMem
import pt.isel.ls.services.ClubService
import pt.isel.ls.services.UserService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClubServiceTests {
    private val transactionManager = TransactionManagerInMem()
    private val clubService = ClubService(transactionManager)
    private val userService = UserService(transactionManager)

    @BeforeTest
    fun setUp() {
        transactionManager.run {
            it.clubRepo.clear()
            it.userRepo.clear()
        }
    }

    @Test
    fun `create club with valid name and existing owner`() {
        val ownerResult = userService.createUser(Name("owner"), Email("owner@email.com"), "password".toPassword())
        assertTrue(ownerResult.isSuccess)
        val clubResult = clubService.createClub("Sporting".toName(), ownerResult.getOrNull()!!)
        assertTrue(clubResult.isSuccess)
        assertEquals(ownerResult.getOrNull(), clubResult.getOrNull()!!.owner)
    }

    @Test
    fun `create club with duplicate name should fail`() {
        val ownerResult = userService.createUser(Name("Ric"), Email("ric@email.com"), "password".toPassword())
        assertTrue(ownerResult.isSuccess)
        val owner = ownerResult.getOrNull()!!
        val firstClubResult = clubService.createClub("Benfica".toName(), owner)
        assertTrue(firstClubResult.isSuccess)
        val secondClubResult = clubService.createClub("Benfica".toName(), owner)
        assertTrue(secondClubResult.isFailure)
    }

    @Test
    fun `find club by identifier`() {
        val owner = userService.createUser(Name("Ric1"), Email("ric@email.com"), "password".toPassword())
        assertTrue(owner.isSuccess)
        val club = clubService.createClub("Porto".toName(), owner.getOrNull()!!)
        assertTrue(club.isSuccess)
        val foundClub = clubService.getClubById(club.getOrNull()!!.cid)
        assertEquals(club, foundClub)
    }

    @Test
    fun `find all clubs`() {
        val ownerResult = userService.createUser("owner".toName(), "owner@email.com".toEmail(), "password".toPassword())
        assertTrue(ownerResult.isSuccess)
        val owner = ownerResult.getOrNull()!!
        val club1 = clubService.createClub("Braga".toName(), owner)
        assertTrue(club1.isSuccess)
        val club2 = clubService.createClub("Boavista".toName(), owner)
        assertTrue(club2.isSuccess)
        val allClubs = clubService.getClubs(30, 0)
        assertEquals(2, allClubs.getOrNull()?.count)
    }

    @Test
    fun `find all clubs with complete name`() {
        val ownerResult = userService.createUser("owner".toName(), "owner@email.com".toEmail(), "password".toPassword())
        assertTrue(ownerResult.isSuccess)
        val owner = ownerResult.getOrNull()!!
        val club1 = clubService.createClub("Braga".toName(), owner)
        assertTrue(club1.isSuccess)
        val club2 = clubService.createClub("Boavista".toName(), owner)
        assertTrue(club2.isSuccess)

        val allClubs = clubService.getClubs(30, 0, "boavista".toName())
        assertEquals(1, allClubs.getOrNull()?.count)
        assertEquals(club2.getOrNull(), allClubs.getOrNull()?.items?.first())
    }

    @Test
    fun `find all clubs with partial name`() {
        val ownerResult = userService.createUser("owner".toName(), "owner@email.com".toEmail(), "password".toPassword())
        assertTrue(ownerResult.isSuccess)
        val owner = ownerResult.getOrNull()!!
        val club1 = clubService.createClub("Braga".toName(), owner)
        assertTrue(club1.isSuccess)
        val club2 = clubService.createClub("Boavista".toName(), owner)
        assertTrue(club2.isSuccess)

        val allClubs = clubService.getClubs(30, 0, "avis".toName())
        assertEquals(1, allClubs.getOrNull()?.count)
        assertEquals(club2.getOrNull(), allClubs.getOrNull()?.items?.first())
    }
}
