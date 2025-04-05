package pt.isel.ls.webapi.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Rental

@Serializable
data class RentalOutput(
    val rid: UInt,
    val date: LocalDate,
) {
    constructor(rental: Rental) :
        this(
            rental.rid,
            rental.date,
        )
}
