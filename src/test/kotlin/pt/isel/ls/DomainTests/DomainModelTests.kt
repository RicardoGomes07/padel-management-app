package pt.isel.ls.DomainTests

import org.junit.Test
import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.User
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DomainModelTests {
    @Test
    fun testCreateUser_with_wrongEmail() {
        assertFailsWith<IllegalArgumentException> {
            User(
                uid = 1u,
                name = Name("Ricardo"),
                email = Email("ric.gmail.com"),
                token = Uuid.random(),
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
                token = Uuid.random(),
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
                token = Uuid.random(),
            )
        )
    }
}