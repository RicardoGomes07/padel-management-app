package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class AvailableHours(
    val hours: List<UIntRange>,
)

fun List<UInt>.toAvailableHours(): AvailableHours {
    val sorted = this.sorted()

    val ranges =
        sorted
            .fold(emptyList<Pair<UInt, UInt>>()) { acc, current ->
                when {
                    acc.isEmpty() -> listOf(current to current)
                    acc.last().second + 1u == current -> acc.dropLast(1) + (acc.last().first to current)
                    else -> acc + (current to current)
                }
            }

    return AvailableHours(ranges.map { (start, end) -> UIntRange(start, end) })
}

@Serializable
data class UIntRange(
    val start: UInt,
    val end: UInt,
) {
    override fun toString(): String = "$start..$end"
}
