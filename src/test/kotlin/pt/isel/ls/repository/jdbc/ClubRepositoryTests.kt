@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.services.ClubError
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*

class ClubRepositoryTests {
    private val connection: Connection = DriverManager.getConnection(DB_URL)

    private val clubRepoJdbc = ClubRepositoryJdbc(connection)
    private val userRepoJdbc = UserRepositoryJdbc(connection)

    @BeforeTest
    fun setUp() {
        clubRepoJdbc.clear()
        userRepoJdbc.clear()
    }

    @Test
    fun `create club with duplicate name should fail`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail())
        clubRepoJdbc.createClub("The King of Padel".toName(), owner.uid)

        assertFailsWith<ClubError.ClubAlreadyExists> {
            clubRepoJdbc.createClub("The King of Padel".toName(), owner.uid)
        }
    }

    @Test
    fun `create club with non-existent owner should fail`() {
        assertFailsWith<ClubError.OwnerNotFound> {
            clubRepoJdbc.createClub("Nonexistent Owner Club".toName(), 999u)
        }
    }

    @Test
    fun `find club by name`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoJdbc.createClub("Force Club".toName(), owner.uid)

        val foundClub = clubRepoJdbc.findClubByName("Force Club".toName())
        assertEquals(club, foundClub)
    }

    @Test
    fun `find club by non-existent name should return null`() {
        val foundClub = clubRepoJdbc.findClubByName("Nonexistent Club".toName())
        assertNull(foundClub)
    }

    @Test
    fun `find club by identifier`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoJdbc.createClub("Fly Club".toName(), owner.uid)

        val foundClub = clubRepoJdbc.findByIdentifier(club.cid)
        assertEquals(club, foundClub)
    }

    @Test
    fun `find all clubs`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail())
        val club1 = clubRepoJdbc.createClub("Force Club".toName(), owner.uid)
        val club2 = clubRepoJdbc.createClub("Fly Club".toName(), owner.uid)

        val allClubs = clubRepoJdbc.findAll()
        assertEquals(2, allClubs.size)
        assertTrue(allClubs.containsAll(listOf(club1, club2)))
    }

    @Test
    fun `delete club by identifier`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoJdbc.createClub("Force Club".toName(), owner.uid)
        assertEquals(1, clubRepoJdbc.findAll().size)

        clubRepoJdbc.deleteByIdentifier(club.cid)
        assertNull(clubRepoJdbc.findByIdentifier(club.cid))
    }

    @Test
    fun `save updates existing club`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoJdbc.createClub("Fly Club".toName(), owner.uid)

        val updatedClub = club.copy(name = "Updated Fly Club".toName())
        clubRepoJdbc.save(updatedClub)

        val retrievedClub = clubRepoJdbc.findClubByName(updatedClub.name)
        assertEquals("Updated Fly Club".toName(), retrievedClub?.name)
    }
}
