@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.mem

import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.repository.jdbc.ClubRepositoryJdbc
import pt.isel.ls.repository.jdbc.DB_URL
import pt.isel.ls.repository.jdbc.UserRepositoryJdbc
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*

class ClubRepositoryTests {
    private val clubRepoInMem = ClubRepositoryInMem
    private val userRepoInMem = UserRepositoryInMem

    private val connection: Connection = DriverManager.getConnection(DB_URL)

    private val clubRepoJdbc = ClubRepositoryJdbc(connection)
    private val userRepoJdbc = UserRepositoryJdbc(connection)

    private val implementations =
        listOf(
            Pair(clubRepoInMem, userRepoInMem),
            Pair(clubRepoJdbc, userRepoJdbc),
        )

    @BeforeTest
    fun setUp() {
        implementations.forEach { (clubRepo, userRepo) ->
            clubRepo.clear()
            userRepo.clear()
        }
    }

    @Test
    fun `create club with valid name and existing owner`() {
        implementations.forEach { (clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            val club = clubRepo.createClub("Parker Club".toName(), owner.uid)

            assertEquals("Parker Club".toName(), club.name)
            assertEquals(owner, club.owner)
        }
    }

    @Test
    fun `create club with duplicate name should fail`() {
        implementations.forEach { (clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            clubRepo.createClub("The King of Padel".toName(), owner.uid)

            assertFailsWith<IllegalArgumentException> {
                clubRepo.createClub("The King of Padel".toName(), owner.uid)
            }
        }
    }

    @Test
    fun `create club with non-existent owner should fail`() {
        implementations.forEach { (clubRepo) ->
            assertFailsWith<IllegalArgumentException> {
                clubRepo.createClub("Nonexistent Owner Club".toName(), 999u)
            }
        }
    }

    @Test
    fun `find club by name`() {
        implementations.forEach { (clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            val club = clubRepo.createClub("Force Club".toName(), owner.uid)

            val foundClub = clubRepo.findClubByName("Force Club".toName())
            assertEquals(club, foundClub)
        }
    }

    @Test
    fun `find club by non-existent name should return null`() {
        implementations.forEach { (clubRepo) ->
            val foundClub = clubRepo.findClubByName("Nonexistent Club".toName())
            assertNull(foundClub)
        }
    }

    @Test
    fun `find club by identifier`() {
        implementations.forEach { (clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            val club = clubRepo.createClub("Fly Club".toName(), owner.uid)

            val foundClub = clubRepo.findByIdentifier(club.cid)
            assertEquals(club, foundClub)
        }
    }

    @Test
    fun `find all clubs`() {
        implementations.forEach { (clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            val club1 = clubRepo.createClub("Force Club".toName(), owner.uid)
            val club2 = clubRepo.createClub("Fly Club".toName(), owner.uid)

            val allClubs = clubRepo.findAll()
            assertEquals(2, allClubs.size)
            assertTrue(allClubs.containsAll(listOf(club1, club2)))
        }
    }

    @Test
    fun `delete club by identifier`() {
        implementations.forEach { (clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            val club = clubRepo.createClub("Force Club".toName(), owner.uid)
            assertEquals(1, clubRepo.findAll().size)

            clubRepo.deleteByIdentifier(club.cid)
            assertEquals(0, clubRepo.findAll().size)
        }
    }

    @Test
    fun `save updates existing club`() {
        implementations.forEach { (clubRepo, userRepo) ->
            val owner = userRepo.createUser("owner".toName(), "owner@email.com".toEmail())
            val club = clubRepo.createClub("Fly Club".toName(), owner.uid)

            val updatedClub = club.copy(name = "Updated Fly Club".toName())
            clubRepo.save(updatedClub)

            val retrievedClub = clubRepo.findByIdentifier(club.cid)
            assertEquals("Updated Fly Club".toName(), retrievedClub?.name)
        }
    }
}
