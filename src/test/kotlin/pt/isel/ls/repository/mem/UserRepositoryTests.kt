@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.mem

import pt.isel.ls.domain.*
import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.repository.jdbc.DB_URL
import pt.isel.ls.repository.jdbc.UserRepositoryJdbc
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*

class UserRepositoryTests {
    private val userRepoInMem = UserRepositoryInMem

    private val connection: Connection = DriverManager.getConnection(DB_URL)
    private val userRepoJdbc = UserRepositoryJdbc(connection)

    private val implementations =
        listOf(
            userRepoInMem,
            userRepoJdbc,
        )

    @BeforeTest
    fun setUp() {
        implementations.forEach { userRepo -> userRepo.clear() }
    }

    @Test
    fun `user creation with valid Name and Email`() {
        implementations.forEach { userRepo ->
            val user = userRepo.createUser("user".toName(), "user@email.com".toEmail())
            assertEquals("user".toName(), user.name)
            assertEquals("user@email.com".toEmail(), user.email)
        }
    }

    @Test
    fun `user creation with invalid Email`() {
        implementations.forEach { userRepo ->
            if(userRepo == userRepoJdbc) {
                userRepo.createUser("user".toName(), "user@email.com".toEmail())
                assertFailsWith<IllegalArgumentException> {
                    userRepo.createUser("user".toName(), "user@email.com".toEmail())
                }
            }
            /*assertEquals(1, userRepo.findAll().size)*/
        }
    }

    @Test
    fun `retrieve user with user token`() {
        implementations.forEach { userRepo ->
            val user1 = userRepo.createUser("user".toName(), "user@email.com".toEmail())
            val user = userRepo.findUserByToken(user1.token)
            assertEquals(user1, user)

            val fakeToken = generateToken()
            val invalidUser = userRepo.findUserByToken(fakeToken)
            assertNull(invalidUser)
        }
    }

    @Test
    fun `find user by identifier`() {
        implementations.forEach { userRepo ->
            val user = userRepo.createUser("testUser".toName(), "test@email.com".toEmail())
            val retrievedUser = userRepo.findByIdentifier(user.uid)
            assertEquals(user, retrievedUser)
        }
    }

    @Test
    fun `find all users`() {
        implementations.forEach { userRepo ->
            val user1 = userRepo.createUser("user1".toName(), "user1@email.com".toEmail())
            val user2 = userRepo.createUser("user2".toName(), "user2@email.com".toEmail())
            val allUsers = userRepo.findAll()
            assertEquals(2, allUsers.size)
            assertTrue(allUsers.containsAll(listOf(user1, user2)))
        }
    }

    @Test
    fun `delete user by identifier`() {
        implementations.forEach { userRepo ->
            val user = userRepo.createUser("deleteUser".toName(), "delete@email.com".toEmail())
            assertEquals(1, userRepo.findAll().size)

            userRepo.deleteByIdentifier(user.uid)
            assertEquals(0, userRepo.findAll().size)
        }
    }

    @Test
    fun `save updates existing user`() {
        implementations.forEach { userRepo ->
            val user = userRepo.createUser("updateUser".toName(), "update@email.com".toEmail())
            val updatedUser = user.copy(name = "updatedUser".toName())
            userRepo.save(updatedUser)

            val retrievedUser = userRepo.findUserByToken(user.token)
            assertEquals("updatedUser".toName(), retrievedUser?.name)
        }
    }

    @Test
    fun `save adds new user when not existing`() {
        implementations.forEach { userRepo ->
            val newUser =
                User(
                    uid = 99u,
                    name = "newUser".toName(),
                    email = "new@email.com".toEmail(),
                    token = generateToken(),
                )
            userRepo.save(newUser)

            val retrievedUser = userRepo.findUserByToken(newUser.token)
            assertEquals(newUser.name, retrievedUser?.name)
            assertEquals(newUser.email, retrievedUser?.email)
            assertEquals(newUser.token, retrievedUser?.token)
        }
    }
}
