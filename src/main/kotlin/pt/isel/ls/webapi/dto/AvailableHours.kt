package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class AvailableHours(
    val hours: List<UIntInterval>,
)

fun List<UInt>.toAvailableHours(): AvailableHours {
    if (isEmpty()) return AvailableHours(emptyList())

    val singleRanges =
        this
            .map { UIntInterval(it, it + 1u) }
            .sortedBy { it.start }

    val mergedRanges = mutableListOf<UIntInterval>()
    var current = singleRanges.first()

    for (i in 1 until singleRanges.size) {
        val next = singleRanges[i]
        if (current.end == next.start) {
            current = UIntInterval(current.start, next.end)
        } else {
            mergedRanges.add(current)
            current = next
        }
    }

    mergedRanges.add(current)

    return AvailableHours(mergedRanges)
}

@Serializable
data class UIntInterval(
    val start: UInt,
    val end: UInt,
) {
    override fun toString(): String = "$start..$end"
}
