package pt.isel.ls.repository.jdbc.dao

import kotlinx.datetime.LocalDate
import pt.isel.ls.domain.TimeSlot
import java.sql.ResultSet

data class RentalDb(
    val rid: UInt,
    val date: LocalDate,
    val rentTime: TimeSlot,
    val renter: UInt,
    val court: UInt,
)

fun ResultSet.mapRentalDb(): RentalDb =
    RentalDb(
        rid = getInt("rental_id").toUInt(),
        date = LocalDate.fromEpochDays(getInt("rental_date")),
        rentTime =
            TimeSlot(
                getInt("rental_start").toUInt(),
                getInt("rental_end").toUInt(),
            ),
        renter = getInt("renter_id").toUInt(),
        court = getInt("court_id").toUInt(),
    )
