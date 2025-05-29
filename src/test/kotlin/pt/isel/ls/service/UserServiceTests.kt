@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.service

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.toPassword
import pt.isel.ls.repository.mem.TransactionManagerInMem
import pt.isel.ls.services.UserService
import kotlin.test.*

class UserServiceTests {
    private val transactionManager = TransactionManagerInMem()
    private val userService = UserService(transactionManager)

    @BeforeTest
    fun setUp() {
        transactionManager.run {
            it.userRepo.clear()
        }
    }

    @Test
    fun `user creation with valid Name and Email`() {
        val userResult = userService.createUser(Name("user"), Email("user@email.com"), "password".toPassword())
        assert(userResult.isSuccess) { "Expected user creation to succeed but got ${userResult.exceptionOrNull()}" }
    }

    @Test
    fun `user creation with invalid Name and Email`() {
        assertFailsWith<IllegalArgumentException> {
            userService.createUser(Name("2131user"), Email("useril.com"), "password".toPassword()).exceptionOrNull()
        }
    }

    @Test
    fun `retrieve user with user token`() {
        val userResult = userService.createUser(Name("user"), Email("user@email.com"), "password".toPassword())
        assertTrue(userResult.isSuccess)
        val createdUser = userResult.getOrNull()!!
        val retrievedUser = userService.validateUser(createdUser.token)
        assertEquals(createdUser, retrievedUser)
    }
}
