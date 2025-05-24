package pt.isel.ls.webapi.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class RentalCreationInput(
    val date: LocalDate,
    val initialHour: UInt,
    val finalHour: UInt,
) {
    init {
        require(initialHour in 0u..23u) { "Invalid initial hour" }
        require(finalHour in 0u..24u) { "Invalid final hour XD" }
        require(initialHour < finalHour) { "Initial hour must be before final hour" }
    }
}
