package pt.isel.ls.domain

data class TimeSlot(
    val start: UInt,
    val end: UInt,
) {
    init {
        require(start < end && end <= 24u) {
            "Invalid time slot: start=$start, end=$end. " +
                "Start must be less than end and both must be between 0 and 24."
        }
    }

    override fun toString(): String = "$start to $end"
}
