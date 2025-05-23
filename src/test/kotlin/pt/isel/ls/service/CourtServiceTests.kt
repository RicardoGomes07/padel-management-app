@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.service

import pt.isel.ls.domain.*
import pt.isel.ls.repository.mem.TransactionManagerInMem
import pt.isel.ls.services.ClubService
import pt.isel.ls.services.CourtService
import pt.isel.ls.services.UserService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CourtServiceTests {
    private val transactionManager = TransactionManagerInMem()
    private val courtService = CourtService(transactionManager)
    private val clubService = ClubService(transactionManager)
    private val userService = UserService(transactionManager)

    @BeforeTest
    fun setUp() {
        transactionManager.run {
            it.courtRepo.clear()
            it.clubRepo.clear()
            it.userRepo.clear()
        }
    }

    @Test
    fun `create court with valid name and existing club`() {
        val ownerResult = userService.createUser("owner".toName(), "owner@email.com".toEmail())
        assertTrue(ownerResult.isSuccess)
        val clubResult = clubService.createClub("Sports Club".toName(), ownerResult.getOrNull()!!)
        assertTrue(clubResult.isSuccess)
        val club = clubResult.getOrNull()!!
        val courtResult = courtService.createCourt("Court A".toName(), club.cid)

        assertTrue(courtResult.isSuccess)
        val court = courtResult.getOrNull()!!

        assertEquals("Court A".toName(), court.name)
        assertEquals(club, court.club)
    }

    @Test
    fun `find courts by club identifier`() {
        val ownerResult = userService.createUser("owner".toName(), "owner@email.com".toEmail())
        assertTrue(ownerResult.isSuccess)
        val clubResult = clubService.createClub("Sports Club".toName(), ownerResult.getOrNull()!!)
        assertTrue(clubResult.isSuccess)
        val club = clubResult.getOrNull()!!
        val court1Result = courtService.createCourt("Court A".toName(), club.cid)
        assertTrue(court1Result.isSuccess)
        val court2Result = courtService.createCourt("Court B".toName(), club.cid)
        assertTrue(court2Result.isSuccess)
        val court1 = court1Result.getOrNull()!!
        val court2 = court2Result.getOrNull()!!
        val foundCourtsResult = courtService.getCourts(club.cid, 30, 0)
        assertTrue(foundCourtsResult.isSuccess)
        val foundCourts = foundCourtsResult.getOrNull()!!
        assertEquals(2, foundCourts.count)

        assertTrue(foundCourts.items.containsAll(listOf(court1, court2)))
    }

    @Test
    fun `find court by identifier`() {
        val ownerResult = userService.createUser(Name("owner"), Email("owner@email.com"))
        assertTrue(ownerResult.isSuccess)
        val clubResult = clubService.createClub("Sports Club".toName(), ownerResult.getOrNull()!!)
        assertTrue(clubResult.isSuccess)
        val courtResult = courtService.createCourt(Name("Court A"), clubResult.getOrNull()!!.cid)

        assertTrue(courtResult.isSuccess)

        val foundCourt = courtService.getCourtById(courtResult.getOrNull()!!.crid)
        assertEquals(courtResult, foundCourt)
    }

    @Test
    fun `find all courts`() {
        val ownerResult = userService.createUser(Name("owner"), Email("owner@email.com"))
        assertTrue(ownerResult.isSuccess)
        val clubResult = clubService.createClub("Sports Club".toName(), ownerResult.getOrNull()!!)
        assertTrue(clubResult.isSuccess)
        val club = clubResult.getOrNull()!!
        val court1Result = courtService.createCourt(Name("Court A"), club.cid)
        assertTrue(court1Result.isSuccess)
        val court2Result = courtService.createCourt(Name("Court B"), club.cid)
        assertTrue(court2Result.isSuccess)
        val court1 = court1Result.getOrNull()!!
        val court2 = court2Result.getOrNull()!!
        val allCourtsResult = courtService.getCourts(club.cid, 30, 0)
        assertTrue(allCourtsResult.isSuccess)
        val allCourts = allCourtsResult.getOrNull()!!
        assertEquals(2, allCourts.count)
        assertTrue(allCourts.items.containsAll(listOf(court1, court2)))
    }
}
