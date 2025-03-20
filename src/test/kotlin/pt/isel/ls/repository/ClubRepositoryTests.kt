@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.repository.mem.ClubRepositoryInMem
import pt.isel.ls.repository.mem.UserRepositoryInMem
import kotlin.test.assertFailsWith

class ClubRepositoryTests {
    private val clubRepo = ClubRepositoryInMem
    private val userRepo = UserRepositoryInMem

    @Before
    fun setUp() {
        clubRepo.clear()
        userRepo.clear()
    }

    @Test
    fun `create club with valid name and existing owner`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        val club = clubRepo.createClub("Parker Club", owner.uid)

        assertEquals(Name("Parker Club"), club.name)
        assertEquals(owner, club.owner)
    }

    @Test
    fun `create club with duplicate name should fail`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        clubRepo.createClub("The King of Padel", owner.uid)

        assertFailsWith<IllegalArgumentException> {
            clubRepo.createClub("The King of Padel", owner.uid)
        }
    }

    @Test
    fun `create club with non-existent owner should fail`() {
        assertFailsWith<IllegalArgumentException> {
            clubRepo.createClub("Nonexistent Owner Club", 999u)
        }
    }

    @Test
    fun `find club by name`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        val club = clubRepo.createClub("Force Club", owner.uid)

        val foundClub = clubRepo.findClubByName("Force Club")
        assertEquals(club, foundClub)
    }

    @Test
    fun `find club by non-existent name should return null`() {
        val foundClub = clubRepo.findClubByName("Nonexistent Club")
        assertNull(foundClub)
    }

    @Test
    fun `find club by identifier`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        val club = clubRepo.createClub("Fly Club", owner.uid)

        val foundClub = clubRepo.findByIdentifier(club.cid)
        assertEquals(club, foundClub)
    }

    @Test
    fun `find all clubs`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        val club1 = clubRepo.createClub("Force Club", owner.uid)
        val club2 = clubRepo.createClub("Fly Club", owner.uid)

        val allClubs = clubRepo.findAll()
        assertEquals(2, allClubs.size)
        assertTrue(allClubs.containsAll(listOf(club1, club2)))
    }

    @Test
    fun `delete club by identifier`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        val club = clubRepo.createClub("Force Club", owner.uid)
        assertEquals(1, clubRepo.findAll().size)

        clubRepo.deleteByIdentifier(club.cid)
        assertEquals(0, clubRepo.findAll().size)
    }

    @Test
    fun `save updates existing club`() {
        val owner = userRepo.createUser(Name("owner"), Email("owner@email.com"))
        val club = clubRepo.createClub("Fly Club", owner.uid)

        val updatedClub = club.copy(name = Name("Updated Fly Club"))
        clubRepo.save(updatedClub)

        val retrievedClub = clubRepo.findByIdentifier(club.cid)
        assertEquals(Name("Updated Fly Club"), retrievedClub?.name)
    }
}
