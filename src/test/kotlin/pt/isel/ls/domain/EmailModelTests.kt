package pt.isel.ls.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class EmailModelTests {
    @Test
    fun `valid email creation`() {
        assertNotNull(Email("test1@gmail.com"))
        assertNotNull(Email("test2@outlook.pt"))
        assertNotNull(Email("test3@hotmail.org"))
    }

    @Test
    fun `test valid emails for toEmail function`() {
        assertNotNull("test1@gmail.com".toEmail())
        assertNotNull("test2@outlook.pt".toEmail())
        assertNotNull("test3@hotmail.org".toEmail())
    }

    @Test
    fun `invalid email creation`() {
        val exception1 = assertFailsWith<IllegalArgumentException> { Email("test1.gmail.com") }
        assertEquals("Email is either misformatted or is too long.", exception1.message)

        val exception2 = assertFailsWith<IllegalArgumentException> { Email("test1@gmail@com") }
        assertEquals("Email is either misformatted or is too long.", exception2.message)

        val exception3 = assertFailsWith<IllegalArgumentException> { Email("test1gmail.com") }
        assertEquals("Email is either misformatted or is too long.", exception3.message)

        val exception4 = assertFailsWith<IllegalArgumentException> { Email("test4.gmail.com".repeat(300)) }
        assertEquals("Email is either misformatted or is too long.", exception4.message)
    }

    @Test
    fun `invalid email creation with toEmail`() {
        val exception1 = assertFailsWith<IllegalArgumentException> { "test1.gmail.com".toEmail() }
        assertEquals("Email is either misformatted or is too long.", exception1.message)

        val exception2 = assertFailsWith<IllegalArgumentException> { "test1@gmail@com".toEmail() }
        assertEquals("Email is either misformatted or is too long.", exception2.message)

        val exception3 = assertFailsWith<IllegalArgumentException> { "test1gmail.com".toEmail() }
        assertEquals("Email is either misformatted or is too long.", exception3.message)

        val exception4 = assertFailsWith<IllegalArgumentException> { "test4.gmail.com".repeat(300).toEmail() }
        assertEquals("Email is either misformatted or is too long.", exception4.message)
    }
}
