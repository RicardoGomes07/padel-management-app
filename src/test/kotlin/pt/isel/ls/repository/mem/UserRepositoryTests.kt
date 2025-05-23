@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.mem

import pt.isel.ls.domain.*
import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.services.UserError
import kotlin.test.*

class UserRepositoryTests {
    private val userRepoInMem = UserRepositoryInMem

    @BeforeTest
    fun setUp() {
        userRepoInMem.clear()
    }

    @Test
    fun `user creation with valid Name and Email`() {
        val user = userRepoInMem.createUser("user".toName(), "user@email.com".toEmail())
        assertEquals("user".toName(), user.name)
        assertEquals("user@email.com".toEmail(), user.email)
    }

    @Test
    fun `user creation with invalid Email`() {
        userRepoInMem.createUser("user".toName(), "user@email.com".toEmail())
        assertFailsWith<UserError.UserAlreadyExists> {
            userRepoInMem.createUser("user".toName(), "user@email.com".toEmail())
        }
        assertEquals(1, userRepoInMem.findAll().count)
    }

    @Test
    fun `retrieve user with user token`() {
        val user1 = userRepoInMem.createUser("user".toName(), "user@email.com".toEmail())
        val user = userRepoInMem.findUserByToken(user1.token)
        assertEquals(user1, user)

        val fakeToken = generateToken()
        val invalidUser = userRepoInMem.findUserByToken(fakeToken)
        assertNull(invalidUser)
    }

    @Test
    fun `find user by identifier`() {
        val user = userRepoInMem.createUser("testUser".toName(), "test@email.com".toEmail())
        val retrievedUser = userRepoInMem.findByIdentifier(user.uid)
        assertEquals(user, retrievedUser)
    }

    @Test
    fun `find all users`() {
        val user1 = userRepoInMem.createUser("user1".toName(), "user1@email.com".toEmail())
        val user2 = userRepoInMem.createUser("user2".toName(), "user2@email.com".toEmail())
        val allUsers = userRepoInMem.findAll()
        assertEquals(2, allUsers.count)
        assertTrue(allUsers.items.containsAll(listOf(user1, user2)))
    }

    @Test
    fun `delete user by identifier`() {
        val user = userRepoInMem.createUser("deleteUser".toName(), "delete@email.com".toEmail())
        assertEquals(1, userRepoInMem.findAll().count)

        userRepoInMem.deleteByIdentifier(user.uid)
        assertEquals(0, userRepoInMem.findAll().count)
    }

    @Test
    fun `save updates existing user`() {
        val user = userRepoInMem.createUser("updateUser".toName(), "update@email.com".toEmail())
        val updatedUser = user.copy(name = "updatedUser".toName())
        userRepoInMem.save(updatedUser)

        val retrievedUser = userRepoInMem.findUserByToken(user.token)
        assertEquals("updatedUser".toName(), retrievedUser?.name)
    }

    @Test
    fun `save adds new user when not existing`() {
        val newUser =
            User(
                uid = 99u,
                name = "newUser".toName(),
                email = "new@email.com".toEmail(),
                token = generateToken(),
            )
        userRepoInMem.save(newUser)

        val retrievedUser = userRepoInMem.findUserByToken(newUser.token)
        assertEquals(newUser.name, retrievedUser?.name)
        assertEquals(newUser.email, retrievedUser?.email)
        assertEquals(newUser.token, retrievedUser?.token)
    }
}
