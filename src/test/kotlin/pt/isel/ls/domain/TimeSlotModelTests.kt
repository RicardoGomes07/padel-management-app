package pt.isel.ls.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class TimeSlotModelTests {
    @Test
    fun `invalid time slot throws exception`() {
        val exception1 =
            assertFailsWith<IllegalArgumentException> {
                TimeSlot(10u, 9u)
            }
        assertEquals(
            "Invalid time slot: start=10, end=9. Start must be less than end and both must be between 0 and 24.",
            exception1.message,
        )

        val exception2 =
            assertFailsWith<IllegalArgumentException> {
                TimeSlot(25u, 26u)
            }
        assertEquals(
            "Invalid time slot: start=25, end=26. Start must be less than end and both must be between 0 and 24.",
            exception2.message,
        )
    }

    @Test
    fun `valid time slot does not throw exception`() {
        assertNotNull(TimeSlot(9u, 10u))
        assertNotNull(TimeSlot(0u, 24u))
        assertNotNull(TimeSlot(12u, 18u))
    }

    @Test
    fun `validate toString function`() {
        val timeSlot = TimeSlot(9u, 10u)
        assertEquals("9 to 10", timeSlot.toString())
    }
}
