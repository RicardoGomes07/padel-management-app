package pt.isel.ls.service

import org.junit.Before
import org.junit.Test
import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.repository.mem.UserRepositoryInMem
import pt.isel.ls.services.Failure
import pt.isel.ls.services.Success
import pt.isel.ls.services.UserService
import kotlin.test.*
import kotlin.uuid.ExperimentalUuidApi

class UserServiceTests {
    private val userService = UserService(UserRepositoryInMem)

    @Before
    fun setUp() {
        UserRepositoryInMem.clear()
    }

    @Test
    fun `user creation with valid Name and Email`() {
        val user = userService.createUser(Name("user"), Email("user@email.com"))
        assert(user is Success) { "Expected user creation to succeed but got $user" }
    }

    @Test
    fun `user creation with invalid Name and Email`() {
        assertFailsWith<IllegalArgumentException> {
            userService.createUser(Name("2131user"), Email("useril.com"))
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `retrieve user with user token`() {
        val user = userService.createUser(Name("user"), Email("user@email.com"))
        assertTrue(user is Success)
        val createdUser = user.value
        val retrievedUser = userService.validateUser(createdUser.token)
        assertEquals(createdUser, retrievedUser)
    }
}