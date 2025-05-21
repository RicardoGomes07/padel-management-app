package pt.isel.ls.domain

data class TimeSlot(
    val start: UInt,
    val end: UInt,
) {
    init {
        require(start < end && end < 24u)
    }

    override fun toString(): String = "$start to $end"
}
