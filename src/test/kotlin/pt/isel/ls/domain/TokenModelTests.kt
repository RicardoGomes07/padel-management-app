package pt.isel.ls.domain

import kotlin.test.Test
import kotlin.test.assertNotNull

class TokenModelTests {
    @Test
    fun `there is no 2 equal tokens`() {
        val token1 = generateToken()
        val token2 = generateToken()
        assertNotNull(token1)
        assertNotNull(token2)
        assert(token1 != token2) { "Tokens should be unique" }
    }
}
