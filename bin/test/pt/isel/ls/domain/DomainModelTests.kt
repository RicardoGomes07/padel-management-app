package pt.isel.ls.domain

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class DomainModelTests {
    @Test
    fun testCreateUser_with_wrongEmail() {
        assertFailsWith<IllegalArgumentException> {
            User(
                uid = 1u,
                name = Name("Ricardo"),
                email = Email("ric.gmail.com"),
                token = generateToken(),
            )
        }
    }

    @Test
    fun testCreateUser_with_wrongName() {
        assertFailsWith<IllegalArgumentException> {
            User(
                uid = 1u,
                name = Name(""),
                email = Email("ric@gmail.com"),
                token = generateToken(),
            )
        }
    }

    @Test
    fun createUser_with_success() {
        assertNotNull(
            User(
                uid = 1u,
                name = Name("Ricardo"),
                email = Email("ric@gmail.com"),
                token = generateToken(),
            ),
        )
    }
}
