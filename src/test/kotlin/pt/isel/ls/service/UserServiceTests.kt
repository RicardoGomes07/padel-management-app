@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.service

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.repository.mem.UserRepositoryInMem
import pt.isel.ls.services.UserService
import kotlin.test.*

class UserServiceTests {
    private val userService = UserService(UserRepositoryInMem)

    @BeforeTest
    fun setUp() {
        UserRepositoryInMem.clear()
    }

    @Test
    fun `user creation with valid Name and Email`() {
        val userResult = userService.createUser(Name("user"), Email("user@email.com"))
        assert(userResult.isSuccess) { "Expected user creation to succeed but got ${userResult.exceptionOrNull()}" }
    }

    @Test
    fun `user creation with invalid Name and Email`() {
        assertFailsWith<IllegalArgumentException> {
            userService.createUser(Name("2131user"), Email("useril.com")).exceptionOrNull()
        }
    }

    @Test
    fun `retrieve user with user token`() {
        val userResult = userService.createUser(Name("user"), Email("user@email.com"))
        assertTrue(userResult.isSuccess)
        val createdUser = userResult.getOrNull()!!
        val retrievedUser = userService.validateUser(createdUser.token)
        assertEquals(createdUser, retrievedUser)
    }
}
