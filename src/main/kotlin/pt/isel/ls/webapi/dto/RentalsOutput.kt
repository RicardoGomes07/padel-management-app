package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class RentalsOutput(
    val rentals: List<RentalDetailsOutput>,
)
