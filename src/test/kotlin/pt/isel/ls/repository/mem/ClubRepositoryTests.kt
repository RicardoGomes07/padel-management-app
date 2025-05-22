@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.mem

import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.services.ClubError
import kotlin.test.*

class ClubRepositoryTests {
    private val clubRepoInMem = ClubRepositoryInMem
    private val userRepoInMem = UserRepositoryInMem

    @BeforeTest
    fun setUp() {
        clubRepoInMem.clear()
        userRepoInMem.clear()
    }

    @Test
    fun `create club with valid name and existing owner`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoInMem.createClub("Parker Club".toName(), owner.uid)

        assertEquals("Parker Club".toName(), club.name)
        assertEquals(owner, club.owner)
    }

    @Test
    fun `create club with duplicate name should fail`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail())
        clubRepoInMem.createClub("The King of Padel".toName(), owner.uid)

        assertFailsWith<ClubError.ClubAlreadyExists> {
            clubRepoInMem.createClub("The King of Padel".toName(), owner.uid)
        }
    }

    @Test
    fun `create club with non-existent owner should fail`() {
        assertFailsWith<ClubError.OwnerNotFound> {
            clubRepoInMem.createClub("Nonexistent Owner Club".toName(), 999u)
        }
    }

    @Test
    fun `find club by name`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoInMem.createClub("Force Club".toName(), owner.uid)

        val foundClub = clubRepoInMem.findClubByName("Force Club".toName())
        assertEquals(club, foundClub)
    }

    @Test
    fun `find club by non-existent name should return null`() {
        val foundClub = clubRepoInMem.findClubByName("Nonexistent Club".toName())
        assertNull(foundClub)
    }

    @Test
    fun `find club by identifier`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoInMem.createClub("Fly Club".toName(), owner.uid)

        val foundClub = clubRepoInMem.findByIdentifier(club.cid)
        assertEquals(club, foundClub)
    }

    @Test
    fun `find all clubs`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail())
        val club1 = clubRepoInMem.createClub("Force Club".toName(), owner.uid)
        val club2 = clubRepoInMem.createClub("Fly Club".toName(), owner.uid)

        val allClubs = clubRepoInMem.findAll()
        val numOfClubs = clubRepoInMem.count()
        assertEquals(2, numOfClubs)
        assertTrue(allClubs.containsAll(listOf(club1, club2)))
    }

    @Test
    fun `delete club by identifier`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoInMem.createClub("Force Club".toName(), owner.uid)
        assertEquals(1, clubRepoInMem.findAll().size)

        clubRepoInMem.deleteByIdentifier(club.cid)
        assertEquals(0, clubRepoInMem.findAll().size)
    }

    @Test
    fun `save updates existing club`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail())
        val club = clubRepoInMem.createClub("Fly Club".toName(), owner.uid)

        val updatedClub = club.copy(name = "Updated Fly Club".toName())
        clubRepoInMem.save(updatedClub)

        val retrievedClub = clubRepoInMem.findClubByName(updatedClub.name)
        assertEquals("Updated Fly Club".toName(), retrievedClub?.name)
    }

    @Test
    fun `find all clubs by complete name`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail())
        val club1 = clubRepoInMem.createClub("Force Club".toName(), owner.uid)
        clubRepoInMem.createClub("Fly Club".toName(), owner.uid)

        val allClubs = clubRepoInMem.findClubsByName("Force Club".toName())
        assertEquals(1, allClubs.size)
        assertEquals(allClubs.first(), club1)
    }

    @Test
    fun `find all clubs by partial name`() {
        val owner = userRepoInMem.createUser("owner".toName(), "owner@email.com".toEmail())
        val club1 = clubRepoInMem.createClub("Force Club".toName(), owner.uid)
        clubRepoInMem.createClub("Fly Club".toName(), owner.uid)

        val allClubsWithName = clubRepoInMem.findClubsByName("Force".toName())
        assertEquals(1, allClubsWithName.size)
        assertEquals(allClubsWithName.first(), club1)

        val allClubsWithName2 = clubRepoInMem.findClubsByName("ce Clu".toName())
        assertEquals(1, allClubsWithName2.size)
        assertEquals(allClubsWithName2.first(), club1)
    }
}
