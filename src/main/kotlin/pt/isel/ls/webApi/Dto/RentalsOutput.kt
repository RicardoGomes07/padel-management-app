package pt.isel.ls.WebApi.Dto

import kotlinx.serialization.Serializable

@Serializable
data class RentalsOutput(
    val rentals: List<RentalDetailsOutput>
)