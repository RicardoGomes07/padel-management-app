package pt.isel.ls.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class NameModelTests {
    @Test
    fun `valid name creation`() {
        assertNotNull(Name("test1"))
    }

    @Test
    fun `test valid name for toName function`() {
        assertNotNull("test1".toName())
    }

    @Test
    fun `invalid name creation`() {
        val exception1 = assertFailsWith<IllegalArgumentException> { Name("") }
        assertEquals("Name is either empty or too long.", exception1.message)

        val exception2 = assertFailsWith<IllegalArgumentException> { Name("x".repeat(300)) }
        assertEquals("Name is either empty or too long.", exception2.message)
    }

    @Test
    fun `invalid name creation with toName`() {
        val exception1 = assertFailsWith<IllegalArgumentException> { "".toName() }
        assertEquals("Name is either empty or too long.", exception1.message)

        val exception2 = assertFailsWith<IllegalArgumentException> { "x".repeat(300).toName() }
        assertEquals("Name is either empty or too long.", exception2.message)
    }
}
