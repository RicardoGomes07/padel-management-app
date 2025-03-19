package pt.isel.ls.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.repository.mem.ClubRepositoryInMem
import pt.isel.ls.repository.mem.CourtRepositoryInMem
import pt.isel.ls.repository.mem.UserRepositoryInMem
import kotlin.test.Test
import kotlin.test.assertFailsWith

class CourtRepositoryTests {
    private val courtRepo = CourtRepositoryInMem
    private val clubRepo = ClubRepositoryInMem
    private val userRepo = UserRepositoryInMem

    @Before
    fun setUp() {
        courtRepo.clear()
        clubRepo.clear()
        userRepo.clear()
    }

    @Test
    fun `create court with valid name and existing club`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        val club = clubRepo.createClub("Sports Club", owner.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        assertEquals(Name("Court A"), court.name)
        assertEquals(club, court.club)
    }

    @Test
    fun `create court with non-existent club should fail`() {
        assertFailsWith<IllegalArgumentException> {
            courtRepo.createCourt(Name("Court A"), 999u)
        }
    }

    @Test
    fun `find courts by club identifier`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        val club = clubRepo.createClub("Sports Club", owner.uid)
        val court1 = courtRepo.createCourt(Name("Court A"), club.cid)
        val court2 = courtRepo.createCourt(Name("Court B"), club.cid)

        val foundCourts = courtRepo.findByClubIdentifier(club.cid)
        assertEquals(2, foundCourts.size)
        assertTrue(foundCourts.containsAll(listOf(court1, court2)))
    }

    @Test
    fun `find courts by non-existent club identifier should return empty list`() {
        val foundCourts = courtRepo.findByClubIdentifier(999u)
        assertTrue(foundCourts.isEmpty())
    }

    @Test
    fun `find court by identifier`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        val club = clubRepo.createClub("Sports Club", owner.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val foundCourt = courtRepo.findByIdentifier(court.crid)
        assertEquals(court, foundCourt)
    }

    @Test
    fun `find all courts`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        val club = clubRepo.createClub("Sports Club", owner.uid)
        val court1 = courtRepo.createCourt(Name("Court A"), club.cid)
        val court2 = courtRepo.createCourt(Name("Court B"), club.cid)

        val allCourts = courtRepo.findAll()
        assertEquals(2, allCourts.size)
        assertTrue(allCourts.containsAll(listOf(court1, court2)))
    }

    @Test
    fun `delete court by identifier`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        val club = clubRepo.createClub("Sports Club", owner.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)
        assertEquals(1, courtRepo.findAll().size)

        courtRepo.deleteByIdentifier(court.crid)
        assertEquals(0, courtRepo.findAll().size)
    }

    @Test
    fun `save updates existing court`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        val club = clubRepo.createClub("Sports Club", owner.uid)
        val court = courtRepo.createCourt(Name("Court A"), club.cid)

        val updatedCourt = court.copy(name = Name("Updated Court A"))
        courtRepo.save(updatedCourt)

        val retrievedCourt = courtRepo.findByIdentifier(court.crid)
        assertEquals(Name("Updated Court A"), retrievedCourt?.name)
    }
}