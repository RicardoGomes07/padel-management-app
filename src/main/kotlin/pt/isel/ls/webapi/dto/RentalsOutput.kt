package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Rental

@Serializable
data class RentalsOutput(
    val rentals: List<RentalOutput>,
)

fun List<Rental>.toRentalsOutput() = RentalsOutput(this.map { RentalOutput(it) })
