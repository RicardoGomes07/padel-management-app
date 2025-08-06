package pt.isel.ls.webapi.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Rental

@Serializable
data class RentalOutput(
    val rid: UInt,
    val uid: UInt,
    val cid: UInt,
    val crid: UInt,
    val date: LocalDate,
    val initialHour: UInt,
    val finalHour: UInt,
) {
    constructor(rental: Rental) :
        this(
            rental.rid,
            rental.renter.uid,
            rental.court.club.cid,
            rental.court.crid,
            rental.date,
            rental.rentTime.start,
            rental.rentTime.end,
        )
}
