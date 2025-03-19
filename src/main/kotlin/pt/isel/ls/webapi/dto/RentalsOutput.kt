package pt.isel.ls.webApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class RentalsOutput(
    val rentals: List<RentalDetailsOutput>,
)
