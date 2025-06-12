package pt.isel.ls.domain

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

fun createPassword(passwordLetter: String) = Password(passwordLetter.repeat(44))

class PasswordModelTests {
    @Test
    fun `invalid password throws exception`() {
        assertFailsWith<IllegalArgumentException> {
            Password("short")
        }
        assertFailsWith<IllegalArgumentException> {
            Password("x".repeat(100))
        }
        assertFailsWith<IllegalArgumentException> {
            Password("")
        }
    }

    @Test
    fun `valid password does not throw exception`() {
        assertNotNull(Password("f".repeat(44)))
    }

    @Test
    fun `valid password does not throw exception with toPassword function`() {
        assertNotNull("f".repeat(44).toPassword())
    }
}
