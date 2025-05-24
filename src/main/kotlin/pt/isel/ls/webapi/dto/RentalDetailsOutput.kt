package pt.isel.ls.webapi.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Rental

@Serializable
data class RentalDetailsOutput(
    val rid: UInt,
    val court: CourtOutput,
    val renter: UserDetails,
    val date: LocalDate,
    val initialHour: Int,
    val finalHour: Int,
) {
    init {
        require(initialHour in 0..23) { "Invalid initial hour" }
        require(finalHour in 0..24) { "Invalid final hour" }
        require(initialHour < finalHour) { "Initial hour must be before final hour" }
    }
    constructor(rental: Rental) :
        this(
            rental.rid,
            CourtOutput(
                rental.court,
            ),
            UserDetails(rental.renter),
            rental.date,
            rental.rentTime.start.toInt(),
            rental.rentTime.end.toInt(),
        )
}
