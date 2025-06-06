@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.service

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.createPassword
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
        val userResult = userService.createUser(Name("user"), Email("user@email.com"), createPassword("a"))
        assert(userResult.isSuccess) { "Expected user creation to succeed but got ${userResult.exceptionOrNull()}" }
    }

    @Test
    fun `user creation with invalid Name and Email`() {
        assertFailsWith<IllegalArgumentException> {
            userService.createUser(Name("2131user"), Email("useril.com"), createPassword("a")).exceptionOrNull()
        }
    }

    @Test
    fun `retrieve user with user token`() {
        val userResult = userService.createUser(Name("user"), Email("user@email.com"), createPassword("a"))
        assertTrue(userResult.isSuccess)
        val createdUser = userResult.getOrNull()!!
        val loginResult = userService.login(createdUser.email, createdUser.password)
        assertTrue(loginResult.isSuccess)
        val loggedIn = loginResult.getOrNull()
        assertNotNull(loggedIn)
        assertNotNull(loggedIn.token)
        val retrievedUser = userService.validateUser(loggedIn.token)
        assertEquals(loggedIn, retrievedUser)
    }

    @Test
    fun `login of a user`() {
        val userResult = userService.createUser(Name("user"), Email("user@email.com"), createPassword("a"))
        assert(userResult.isSuccess) { "Expected user creation to succeed but got ${userResult.exceptionOrNull()}" }
        val user = userResult.getOrNull()!!
        assertNull(user.token)
        val loginResult = userService.login(user.email, user.password)
        assert(userResult.isSuccess) { "Expected login to succeed but got ${loginResult.exceptionOrNull()}" }
        val loggedIn = loginResult.getOrNull()!!
        assertNotNull(loggedIn.token)
    }

    @Test
    fun `login and logout of a user`() {
        val userResult = userService.createUser(Name("user"), Email("user@email.com"), createPassword("a"))
        assert(userResult.isSuccess) { "Expected user creation to succeed but got ${userResult.exceptionOrNull()}" }
        val user = userResult.getOrNull()!!
        assertNull(user.token)
        val loginResult = userService.login(user.email, user.password)
        assert(loginResult.isSuccess) { "Expected login to succeed but got ${loginResult.exceptionOrNull()}" }
        val loggedIn = loginResult.getOrNull()!!
        assertNotNull(loggedIn.token)
        val logoutResult = userService.logout(loggedIn)
        assert(logoutResult.isSuccess) { "Expected logout to succeed but got ${logoutResult.exceptionOrNull()}" }
        val loggedOut = userService.findUserById(loggedIn.uid)
        assertNotNull(loggedOut)
        assertNull(loggedOut.token)
    }
}
