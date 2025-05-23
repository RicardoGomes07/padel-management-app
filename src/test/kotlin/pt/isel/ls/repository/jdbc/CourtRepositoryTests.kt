@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.services.CourtError
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*

class CourtRepositoryTests {
    private val connection: Connection =
        DriverManager
            .getConnection(
                System.getenv("DB_URL")
                    ?: throw Exception("Missing DB_URL environment variable"),
            )

    private val courtRepoJdbc = CourtRepositoryJdbc(connection)
    private val clubRepoJdbc = ClubRepositoryJdbc(connection)
    private val userRepoJdbc = UserRepositoryJdbc(connection)

    @BeforeTest
    fun setUp() {
        courtRepoJdbc.clear()
        clubRepoJdbc.clear()
        userRepoJdbc.clear()
    }

    @Test
    fun `create court with valid name and existing club`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), owner.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        assertEquals("Court A".toName(), court.name)
        assertEquals(club, court.club)
    }

    @Test
    fun `create court with non-existent club should fail`() {
        assertFailsWith<CourtError.MissingClub> {
            courtRepoJdbc.createCourt("Court A".toName(), 999u)
        }
    }

    @Test
    fun `find courts by club identifier`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), owner.uid)
        val court1 = courtRepoJdbc.createCourt("Court A".toName(), club.cid)
        val court2 = courtRepoJdbc.createCourt("Court B".toName(), club.cid)

        val foundCourtsPageInfo = courtRepoJdbc.findByClubIdentifier(club.cid)
        val foundCourts = foundCourtsPageInfo.items
        val numOfCourts = foundCourtsPageInfo.count
        assertEquals(2, numOfCourts)
        assertTrue(foundCourts.containsAll(listOf(court1, court2)))
    }

    @Test
    fun `find courts by non-existent club identifier should return empty list`() {
        val foundCourts = courtRepoJdbc.findByClubIdentifier(999u)
        assertTrue(foundCourts.items.isEmpty())
    }

    @Test
    fun `find court by identifier`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), owner.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        val foundCourt = courtRepoJdbc.findByIdentifier(court.crid)
        assertEquals(court, foundCourt)
    }

    @Test
    fun `find all courts`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), owner.uid)
        val court1 = courtRepoJdbc.createCourt("Court A".toName(), club.cid)
        val court2 = courtRepoJdbc.createCourt("Court B".toName(), club.cid)

        val allCourtsPageInfo = courtRepoJdbc.findAll()
        val allCourts = allCourtsPageInfo.items
        assertEquals(2, allCourtsPageInfo.count)
        assertTrue(allCourts.containsAll(listOf(court1, court2)))
    }

    @Test
    fun `delete court by identifier`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), owner.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)
        assertEquals(1, courtRepoJdbc.findAll().count)

        courtRepoJdbc.deleteByIdentifier(court.crid)
        assertNull(courtRepoJdbc.findByIdentifier(court.crid))
    }

    @Test
    fun `save updates existing court`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoJdbc.createClub("Sports Club".toName(), owner.uid)
        val court = courtRepoJdbc.createCourt("Court A".toName(), club.cid)

        val updatedCourt = court.copy(name = "Updated Court A".toName())
        courtRepoJdbc.save(updatedCourt)

        val retrievedCourt =
            courtRepoJdbc
                .findByClubIdentifier(court.club.cid)
                .items
                .firstOrNull {
                    it.name == updatedCourt.name
                }
        assertEquals("Updated Court A".toName(), retrievedCourt?.name)
    }
}
