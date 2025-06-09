package pt.isel.ls.domain

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

fun createPassword(passwordLetter: String) = Password(passwordLetter.repeat(44))

class DomainModelTests {
    @Test
    fun testCreateUser_with_wrongEmail() {
        assertFailsWith<IllegalArgumentException> {
            User(
                uid = 1u,
                name = Name("Ricardo"),
                email = Email("ric.gmail.com"),
                password = createPassword("a"),
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
                password = createPassword("b"),
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
                password = createPassword("c"),
                token = generateToken(),
            ),
        )
    }

    @Test
    fun invalid_password_throws_exception() {
        assertFailsWith<IllegalArgumentException> {
            Password("short")
        }
        assertFailsWith<IllegalArgumentException> {
            Password((1..65).joinToString("") { "x" })
        }
        assertFailsWith<IllegalArgumentException> {
            Password("")
        }
    }

    @Test
    fun valid_password_does_not_throw_exception() {
        createPassword("f")
    }

    @Test
    fun invalid_time_slot_throws_exception() {
        assertFailsWith<IllegalArgumentException> {
            TimeSlot(10u, 9u)
        }
        assertFailsWith<IllegalArgumentException> {
            TimeSlot(25u, 26u)
        }
    }

    @Test
    fun valid_time_slot_does_not_throw_exception() {
        assertNotNull(TimeSlot(9u, 10u))
        assertNotNull(TimeSlot(0u, 24u))
        assertNotNull(TimeSlot(12u, 18u))
    }

    @Test
    fun there_is_no_2_equal_tokens() {
        val token1 = generateToken()
        val token2 = generateToken()
        assertNotNull(token1)
        assertNotNull(token2)
        assert(token1 != token2) { "Tokens should be unique" }
    }
}
