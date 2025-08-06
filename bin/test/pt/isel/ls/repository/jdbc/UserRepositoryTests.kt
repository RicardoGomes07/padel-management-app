@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.domain.*
import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.services.UserError
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*

val DB_URL = System.getenv("DB_URL") ?: throw Exception("Missing env var DB_URL")

class UserRepositoryTests {
    private val connection: Connection = DriverManager.getConnection(DB_URL)
    private val userRepoJdbc = UserRepositoryJdbc(connection)

    @BeforeTest
    fun setUp() {
        userRepoJdbc.clear()
    }

    @Test
    fun `user creation with valid Name and Email`() {
        val user = userRepoJdbc.createUser("user".toName(), "user@email.com".toEmail())
        assertEquals("user".toName(), user.name)
        assertEquals("user@email.com".toEmail(), user.email)
    }

    @Test
    fun `user creation with invalid Email`() {
        userRepoJdbc.createUser("user".toName(), "user@email.com".toEmail())
        assertFailsWith<UserError.UserAlreadyExists> {
            userRepoJdbc.createUser("user".toName(), "user@email.com".toEmail())
        }
        assertEquals(1, userRepoJdbc.findAll().size)
    }

    @Test
    fun `retrieve user with user token`() {
        val user1 = userRepoJdbc.createUser("user".toName(), "user@email.com".toEmail())
        val user = userRepoJdbc.findUserByToken(user1.token)
        assertEquals(user1, user)

        val fakeToken = generateToken()
        val invalidUser = userRepoJdbc.findUserByToken(fakeToken)
        assertNull(invalidUser)
    }

    @Test
    fun `find user by identifier`() {
        val user = userRepoJdbc.createUser("testUser".toName(), "test@email.com".toEmail())
        val retrievedUser = userRepoJdbc.findByIdentifier(user.uid)
        assertEquals(user, retrievedUser)
    }

    @Test
    fun `find all users`() {
        val user1 = userRepoJdbc.createUser("user1".toName(), "user1@email.com".toEmail())
        val user2 = userRepoJdbc.createUser("user2".toName(), "user2@email.com".toEmail())
        val allUsers = userRepoJdbc.findAll()
        assertEquals(2, allUsers.size)
        assertTrue(allUsers.containsAll(listOf(user1, user2)))
    }

    @Test
    fun `delete user by identifier`() {
        val user = userRepoJdbc.createUser("deleteUser".toName(), "delete@email.com".toEmail())
        assertEquals(1, userRepoJdbc.findAll().size)

        userRepoJdbc.deleteByIdentifier(user.uid)
        assertEquals(0, userRepoJdbc.findAll().size)
    }

    @Test
    fun `save updates existing user`() {
        val user = userRepoJdbc.createUser("updateUser".toName(), "update@email.com".toEmail())
        val updatedUser = user.copy(name = "updatedUser".toName())
        userRepoJdbc.save(updatedUser)

        val retrievedUser = userRepoJdbc.findUserByToken(user.token)
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
        userRepoJdbc.save(newUser)

        val retrievedUser = userRepoJdbc.findUserByToken(newUser.token)
        assertEquals(newUser.name, retrievedUser?.name)
        assertEquals(newUser.email, retrievedUser?.email)
        assertEquals(newUser.token, retrievedUser?.token)
    }

    @Test
    fun deleteUserAndFailToFindItById() {
        val user = userRepoJdbc.createUser("deleteUser".toName(), "delete@email.com".toEmail())
        assertEquals(1, userRepoJdbc.findAll().size)

        userRepoJdbc.deleteByIdentifier(user.uid)
        assertNull(userRepoJdbc.findByIdentifier(user.uid))
    }
}
