@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository

import org.junit.Assert.assertTrue
import org.junit.Before
import pt.isel.ls.domain.*
import pt.isel.ls.repository.mem.UserRepositoryInMem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class UserRepositoryTests {
    private val userRepo = UserRepositoryInMem

    @Before
    fun setUp() {
        userRepo.clear()
    }

    @Test
    fun `user creation with valid Name and Email`() {
        val user = userRepo.createUser(Name("user"), Email("user@email.com"))
        assertEquals(Name("user"), user.name)
        assertEquals(Email("user@email.com"), user.email)
    }

    @Test
    fun `user creation with invalid Email`() {
        userRepo.createUser(Name("user"), Email("user@email.com"))
        assertFailsWith<IllegalArgumentException> {
            userRepo.createUser(Name("user"), Email("user@email.com"))
        }
        assertEquals(1, userRepo.findAll().size)
    }

    @Test
    fun `retrieve user with user token`() {
        val user1 = userRepo.createUser(Name("user"), Email("user@email.com"))
        val user = userRepo.findUserByToken(user1.token)
        assertEquals(user1, user)

        val fakeToken = generateToken()
        val invalidUser = userRepo.findUserByToken(fakeToken)
        assertNull(invalidUser)
    }

    @Test
    fun `find user by identifier`() {
        val user = userRepo.createUser(Name("testUser"), Email("test@email.com"))
        val retrievedUser = userRepo.findByIdentifier(user.uid)
        assertEquals(user, retrievedUser)
    }

    @Test
    fun `find all users`() {
        val user1 = userRepo.createUser(Name("user1"), Email("user1@email.com"))
        val user2 = userRepo.createUser(Name("user2"), Email("user2@email.com"))
        val allUsers = userRepo.findAll()
        assertEquals(2, allUsers.size)
        assertTrue(allUsers.containsAll(listOf(user1, user2)))
    }

    @Test
    fun `delete user by identifier`() {
        val user = userRepo.createUser(Name("deleteUser"), Email("delete@email.com"))
        assertEquals(1, userRepo.findAll().size)

        userRepo.deleteByIdentifier(user.uid)
        assertEquals(0, userRepo.findAll().size)
    }

    @Test
    fun `save updates existing user`() {
        val user = userRepo.createUser(Name("updateUser"), Email("update@email.com"))
        val updatedUser = user.copy(name = Name("updatedUser"))
        userRepo.save(updatedUser)

        val retrievedUser = userRepo.findByIdentifier(user.uid)
        assertEquals(Name("updatedUser"), retrievedUser?.name)
    }

    @Test
    fun `save adds new user when not existing`() {
        val newUser =
            User(
                uid = 99u,
                name = Name("newUser"),
                email = Email("new@email.com"),
                token = generateToken(),
            )
        userRepo.save(newUser)

        val retrievedUser = userRepo.findByIdentifier(99u)
        assertEquals(newUser, retrievedUser)
    }
}
