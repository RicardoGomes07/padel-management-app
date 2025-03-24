package pt.isel.ls.domain

data class TimeSlot(
    val start: UInt,
    val end: UInt,
) {
    init {
        require(start < end && end < 24u)
    }
}

fun IntRange.toTimeSlot(): TimeSlot = TimeSlot(start.toUInt(), endInclusive.toUInt())
