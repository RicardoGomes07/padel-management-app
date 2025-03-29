@file:Suppress("ktlint:standard:filename")

package pt.isel.ls.services

import kotlinx.datetime.LocalDate
import pt.isel.ls.domain.TimeSlot

private enum class ErrorTypes(
    val description: String,
) {
    USER("Error during user operation"),
    CLUB("Error during club operation"),
    COURT("Error during court operation"),
    RENTAL("Error during rental operation"),
}

sealed class CustomError(
    private val type: ErrorTypes,
    override val message: String,
    val description: String = type.description,
) : RuntimeException(message)

sealed class UserError(
    message: String,
) : CustomError(ErrorTypes.USER, message) {
    class UserAlreadyExists(
        email: String,
    ) : UserError("User with email $email already exists")
}

sealed class ClubError(
    message: String,
) : CustomError(ErrorTypes.CLUB, message) {
    class ClubAlreadyExists(
        name: String,
    ) : ClubError("Club with name $name already exists")

    class OwnerNotFound(
        ownerId: UInt,
    ) : ClubError("Owner with id $ownerId not found")

    class ClubNotFound(
        clubId: UInt,
    ) : ClubError("Club with id $clubId not found")
}

sealed class CourtError(
    message: String,
) : CustomError(ErrorTypes.COURT, message) {
    class CourtNotFound(
        courtId: UInt,
    ) : CourtError("Court with id $courtId not found")

    class MissingClub(
        clubId: UInt,
    ) : CourtError("No club found with id $clubId for court creation")
}

sealed class RentalError(
    message: String,
) : CustomError(ErrorTypes.RENTAL, message) {
    class RentalNotFound(
        rentalId: UInt,
    ) : RentalError("Rental with id $rentalId not found")

    class RentalDateInThePast(
        date: LocalDate,
    ) : RentalError("Rental date $date is in the past")

    class RentalAlreadyExists(
        date: LocalDate,
        timeSlot: TimeSlot,
    ) : RentalError(
            "Rental already exists for date $date with initial hour ${timeSlot.start} and final hour ${timeSlot.end}",
        )

    class RenterNotFound(
        renterId: UInt,
    ) : RentalError("Renter with id $renterId not found")

    class MissingCourt(
        courtId: UInt,
    ) : RentalError("Court with id $courtId not found")
}

fun ensureOrThrow(
    condition: Boolean,
    exception: RuntimeException,
) {
    if (!condition) throw exception
}

fun <T : Any> getOrThrow(
    exception: RuntimeException,
    valueProvider: () -> T?,
): T = valueProvider() ?: throw exception
