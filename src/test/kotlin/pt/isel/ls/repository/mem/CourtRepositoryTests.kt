@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.mem

import pt.isel.ls.domain.createPassword
import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.services.CourtError
import kotlin.test.*

class CourtRepositoryTests {
    private val courtRepoInMem = CourtRepositoryInMem
    private val clubRepoInMem = ClubRepositoryInMem
    private val userRepoInMem = UserRepositoryInMem

    @BeforeTest
    fun setUp() {
        courtRepoInMem.clear()
        clubRepoInMem.clear()
        userRepoInMem.clear()
    }

    @Test
    fun `create court with valid name and existing club`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail(), createPassword("a"))
        val club = clubRepoInMem.createClub("Sports Club".toName(), owner.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        assertEquals("Court A".toName(), court.name)
        assertEquals(club, court.club)
    }

    @Test
    fun `create court with non-existent club should fail`() {
        assertFailsWith<CourtError.MissingClub> {
            courtRepoInMem.createCourt("Court A".toName(), 999u)
        }
    }

    @Test
    fun `find courts by club identifier`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail(), createPassword("a"))
        val club = clubRepoInMem.createClub("Sports Club".toName(), owner.uid)
        val court1 = courtRepoInMem.createCourt("Court A".toName(), club.cid)
        val court2 = courtRepoInMem.createCourt("Court B".toName(), club.cid)

        val foundCourts = courtRepoInMem.findByClubIdentifier(club.cid)
        val numOfCourts = courtRepoInMem.count(club.cid)
        assertEquals(2, numOfCourts)
        assertTrue(foundCourts.items.containsAll(listOf(court1, court2)))
    }

    @Test
    fun `find courts by non-existent club identifier should return empty list`() {
        val foundCourts = courtRepoInMem.findByClubIdentifier(999u)
        assertTrue(foundCourts.items.isEmpty())
    }

    @Test
    fun `find court by identifier`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail(), createPassword("a"))
        val club = clubRepoInMem.createClub("Sports Club".toName(), owner.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        val foundCourt = courtRepoInMem.findByIdentifier(court.crid)
        assertEquals(court, foundCourt)
    }

    @Test
    fun `find all courts`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail(), createPassword("a"))
        val club = clubRepoInMem.createClub("Sports Club".toName(), owner.uid)
        val court1 = courtRepoInMem.createCourt("Court A".toName(), club.cid)
        val court2 = courtRepoInMem.createCourt("Court B".toName(), club.cid)

        val allCourts = courtRepoInMem.findAll()
        assertEquals(2, allCourts.count)
        assertTrue(allCourts.items.containsAll(listOf(court1, court2)))
    }

    @Test
    fun `delete court by identifier`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail(), createPassword("a"))
        val club = clubRepoInMem.createClub("Sports Club".toName(), owner.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)
        assertEquals(1, courtRepoInMem.findAll().count)

        courtRepoInMem.deleteByIdentifier(court.crid)
        assertEquals(0, courtRepoInMem.findAll().count)
    }

    @Test
    fun `save updates existing court`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail(), createPassword("a"))
        val club = clubRepoInMem.createClub("Sports Club".toName(), owner.uid)
        val court = courtRepoInMem.createCourt("Court A".toName(), club.cid)

        val updatedCourt = court.copy(name = "Updated Court A".toName())
        courtRepoInMem.save(updatedCourt)

        val retrievedCourt =
            courtRepoInMem
                .findByClubIdentifier(court.club.cid)
                .items
                .firstOrNull {
                    it.name == updatedCourt.name
                }
        assertEquals("Updated Court A".toName(), retrievedCourt?.name)
    }
}
