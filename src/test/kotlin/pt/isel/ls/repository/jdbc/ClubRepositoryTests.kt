@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.domain.toPassword
import pt.isel.ls.services.ClubError
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*


class ClubRepositoryTests {
    private val connection: Connection =
        DriverManager
            .getConnection(
                System.getenv("DB_URL")
                    ?: throw Exception("Missing DB_URL environment variable"),
            )

    private val clubRepoJdbc = ClubRepositoryJdbc(connection)
    private val userRepoJdbc = UserRepositoryJdbc(connection)

    @BeforeTest
    fun setUp() {
        clubRepoJdbc.clear()
        userRepoJdbc.clear()
    }

    @Test
    fun `create club with duplicate name should fail`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail(), "password".toPassword())
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
    fun `find club by identifier`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail(), "password".toPassword())
        val club = clubRepoJdbc.createClub("Fly Club".toName(), owner.uid)

        val foundClub = clubRepoJdbc.findByIdentifier(club.cid)
        assertEquals(club, foundClub)
    }

    @Test
    fun `find all clubs`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail(), "password".toPassword())
        val club1 = clubRepoJdbc.createClub("Force Club".toName(), owner.uid)
        val club2 = clubRepoJdbc.createClub("Fly Club".toName(), owner.uid)

        val allClubsPageInfo = clubRepoJdbc.findAll()
        val allClubs = allClubsPageInfo.items
        val numOfClubs = allClubsPageInfo.count
        assertEquals(2, numOfClubs)
        assertTrue(allClubs.containsAll(listOf(club1, club2)))
    }

    @Test
    fun `delete club by identifier`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail(), "password".toPassword())
        val club = clubRepoJdbc.createClub("Force Club".toName(), owner.uid)
        assertEquals(1, clubRepoJdbc.findAll().count)

        clubRepoJdbc.deleteByIdentifier(club.cid)
        assertNull(clubRepoJdbc.findByIdentifier(club.cid))
    }

    @Test
    fun `save updates existing club`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail(), "password".toPassword())
        val club = clubRepoJdbc.createClub("Fly Club".toName(), owner.uid)

        val updatedClub = club.copy(name = "Updated Fly Club".toName())
        clubRepoJdbc.save(updatedClub)

        val retrievedClub = clubRepoJdbc.findClubsByName(updatedClub.name)
        assertEquals("Updated Fly Club".toName(), retrievedClub.items.first().name)
    }

    @Test
    fun `find all clubs by complete name`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail(), "password".toPassword())
        val club1 = clubRepoJdbc.createClub("Force Club".toName(), owner.uid)
        clubRepoJdbc.createClub("Fly Club".toName(), owner.uid)

        val allClubsPageInfo = clubRepoJdbc.findClubsByName("Force Club".toName())
        val allClubs = allClubsPageInfo.items
        val numOfClubs = allClubsPageInfo.count
        assertEquals(1, numOfClubs)
        assertEquals(allClubs.first(), club1)
    }

    @Test
    fun `find all clubs by partial name`() {
        val owner = userRepoJdbc.createUser("owner".toName(), "owner@email.com".toEmail(), "password".toPassword())
        val club1 = clubRepoJdbc.createClub("Force Club".toName(), owner.uid)
        clubRepoJdbc.createClub("Fly Club".toName(), owner.uid)

        val allClubsWithNamePageInfo = clubRepoJdbc.findClubsByName("Force".toName())
        val allClubsWithName = allClubsWithNamePageInfo.items
        val numOfClubs = allClubsWithNamePageInfo.count
        assertEquals(1, numOfClubs)
        assertEquals(allClubsWithName.first(), club1)

        val allClubsWithName2PageInfo = clubRepoJdbc.findClubsByName("ce Clu".toName())
        val allClubsWithName2 = allClubsWithName2PageInfo.items
        val numOfClubsWithName2 = allClubsWithName2PageInfo.count
        assertEquals(1, numOfClubsWithName2)
        assertEquals(allClubsWithName2.first(), club1)
    }
}
