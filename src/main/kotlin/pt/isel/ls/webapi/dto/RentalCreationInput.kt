package pt.isel.ls.webapi.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class RentalCreationInput(
    val cid: Int,
    val crid: Int,
    val date: LocalDate,
    val initialHour: Int,
    val finalHour: Int,
) {
    init {
        require(initialHour in 0..23) { "Invalid initial hour" }
        require(finalHour in 0..23) { "Invalid final hour" }
        require(initialHour < finalHour) { "Initial hour must be before final hour" }
    }
}
