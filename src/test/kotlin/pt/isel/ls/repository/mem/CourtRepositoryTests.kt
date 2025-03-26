@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.mem

import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.repository.jdbc.ClubRepositoryJdbc
import pt.isel.ls.repository.jdbc.CourtRepositoryJdbc
import pt.isel.ls.repository.jdbc.DB_URL
import pt.isel.ls.repository.jdbc.UserRepositoryJdbc
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*

class CourtRepositoryTests {
    private val courtRepoInMem = CourtRepositoryInMem
    private val clubRepoInMem = ClubRepositoryInMem
    private val userRepoInMem = UserRepositoryInMem

    private val connection: Connection = DriverManager.getConnection(DB_URL)

    private val courtRepoJdbc = CourtRepositoryJdbc(connection)
    private val clubRepoJdbc = ClubRepositoryJdbc(connection)
    private val userRepoJdbc = UserRepositoryJdbc(connection)

    private val implementations =
        listOf(
            Triple(courtRepoInMem, clubRepoInMem, userRepoInMem),
            Triple(courtRepoJdbc, clubRepoJdbc, userRepoJdbc),
        )

    @BeforeTest
    fun setUp() {
        implementations.forEach { (courtRepo, clubRepo, userRepo) ->
            courtRepo.clear()
            clubRepo.clear()
            userRepo.clear()
        }
    }

    @Test
    fun `create court with valid name and existing club`() {
        implementations.forEach { (courtRepo, clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), owner.uid)
            val court = courtRepo.createCourt("Court A".toName(), club.cid)

            assertEquals("Court A".toName(), court.name)
            assertEquals(club, court.club)
        }
    }

    @Test
    fun `create court with non-existent club should fail`() {
        implementations.forEach { (courtRepo) ->
            assertFailsWith<IllegalArgumentException> {
                courtRepo.createCourt("Court A".toName(), 999u)
            }
        }
    }

    @Test
    fun `find courts by club identifier`() {
        implementations.forEach { (courtRepo, clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), owner.uid)
            val court1 = courtRepo.createCourt("Court A".toName(), club.cid)
            val court2 = courtRepo.createCourt("Court B".toName(), club.cid)

            val foundCourts = courtRepo.findByClubIdentifier(club.cid)
            assertEquals(2, foundCourts.size)
            assertTrue(foundCourts.containsAll(listOf(court1, court2)))
        }
    }

    @Test
    fun `find courts by non-existent club identifier should return empty list`() {
        implementations.forEach { (courtRepo) ->
            val foundCourts = courtRepo.findByClubIdentifier(999u)
            assertTrue(foundCourts.isEmpty())
        }
    }

    @Test
    fun `find court by identifier`() {
        implementations.forEach { (courtRepo, clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), owner.uid)
            val court = courtRepo.createCourt("Court A".toName(), club.cid)

            val foundCourt = courtRepo.findByIdentifier(court.crid)
            assertEquals(court, foundCourt)
        }
    }

    @Test
    fun `find all courts`() {
        implementations.forEach { (courtRepo, clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), owner.uid)
            val court1 = courtRepo.createCourt("Court A".toName(), club.cid)
            val court2 = courtRepo.createCourt("Court B".toName(), club.cid)

            val allCourts = courtRepo.findAll()
            assertEquals(2, allCourts.size)
            assertTrue(allCourts.containsAll(listOf(court1, court2)))
        }
    }

    @Test
    fun `delete court by identifier`() {
        implementations.forEach { (courtRepo, clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), owner.uid)
            val court = courtRepo.createCourt("Court A".toName(), club.cid)
            assertEquals(1, courtRepo.findAll().size)

            courtRepo.deleteByIdentifier(court.crid)
            assertEquals(0, courtRepo.findAll().size)
        }
    }

    @Test
    fun `save updates existing court`() {
        implementations.forEach { (courtRepo, clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            val club = clubRepo.createClub("Sports Club".toName(), owner.uid)
            val court = courtRepo.createCourt("Court A".toName(), club.cid)

            val updatedCourt = court.copy(name = "Updated Court A".toName())
            courtRepo.save(updatedCourt)

            val retrievedCourt =
                courtRepo
                    .findByClubIdentifier(court.club.cid)
                    .firstOrNull {
                        it.name == updatedCourt.name
                    }
            assertEquals("Updated Court A".toName(), retrievedCourt?.name)
        }
    }
}
