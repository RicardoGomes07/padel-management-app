package pt.isel.ls.webApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class RentalsOutput(
    val rentals: List<RentalDetailsOutput>,
)
