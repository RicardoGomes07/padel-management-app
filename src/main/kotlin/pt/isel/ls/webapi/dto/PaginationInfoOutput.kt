package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.PaginationInfo

@Serializable
data class PaginationInfoOutput<T>(
    val items: T,
    val count: Int,
)

// ex.: T is Club and R is ClubsOutput
fun <T, R> PaginationInfo<T>.toPaginationOutput(transformItems: List<T>.() -> R) =
    PaginationInfoOutput(this.items.transformItems(), this.count)
