@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*

val DB_URL = System.getenv("DB_URL") ?: throw Exception("Missing env var DB_URL")

class UserRespoitoryTests {
    private val connection: Connection = DriverManager.getConnection(DB_URL)
    private val userRepository = UserRepositoryJdbc(connection)

    // before each test clear table
    @BeforeTest
    fun setup() {
        userRepository.clear()
    }

    @Test
    fun createUser() {
        val name = Name("Marlon Hoffstadt")
        val email = Email("marlon@gmail.com")

        val user = userRepository.createUser(name, email)

        assertTrue(name == user.name && email == user.email)
    }

    @Test
    fun createUserAndFindIt() {
        val name = Name("Marlon Hoffstadt")
        val email = Email("marlon@gmail.com")

        val user = userRepository.createUser(name, email)

        val foundWithToken = userRepository.findUserByToken(user.token)

        assertNotNull(foundWithToken)

        assertEquals(user, foundWithToken)
    }

    @Test
    fun findAllUsers() {
        val marlonHoffstadt =
            userRepository.createUser(
                Name("Marlon Hoffstadt"),
                Email("marlon@gmail.com"),
            )

        val siennaSleep =
            userRepository.createUser(
                Name("Sienna Sleep"),
                Email("sleep@hotmail.com"),
            )

        val existingUsers = userRepository.findAll()

        assertEquals(existingUsers.size, 2)
    }
}
