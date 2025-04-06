package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Rental

@Serializable
data class RentalsOutput(
    val rentals: List<RentalOutput>,
    val paginationInfo: PaginationInfo,
)

fun List<Rental>.toRentalsOutput(paginationInfo: PaginationInfo) = RentalsOutput(this.map { RentalOutput(it) }, paginationInfo)
