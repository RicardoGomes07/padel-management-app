@file:Suppress("ktlint:standard:filename")

package pt.isel.ls.services

import kotlinx.datetime.LocalDate
import pt.isel.ls.domain.TimeSlot

sealed class CustomError(
    override val message: String,
    val description: String,
) : RuntimeException(message)

sealed class UserError(
    message: String,
) : CustomError(message, "Error during user operation") {
    class UserAlreadyExists(
        email: String,
    ) : UserError("User with email $email already exists")

    class UserFailedLogin : UserError("Email or password wrong.")
}

sealed class ClubError(
    message: String,
) : CustomError(message, "Error during club operation") {
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
) : CustomError(message, "Error during court operation") {
    class CourtNotFound(
        courtId: UInt,
    ) : CourtError("Court with id $courtId not found")

    class MissingClub(
        clubId: UInt,
    ) : CourtError("No club found with id $clubId for court creation")
}

sealed class RentalError(
    message: String,
) : CustomError(message, "Error during rental operation") {
    class RentalNotFound(
        rentalId: UInt,
    ) : RentalError("Rental with id $rentalId not found")

    class RentalDateInThePast(
        date: LocalDate,
    ) : RentalError("Rental date $date is in the past")

    class OverlapInTimeSlot(
        date: LocalDate,
        timeSlot: TimeSlot,
    ) : RentalError(
            "Rental already exists for date $date and the time slot you provided: $timeSlot overlaps with an existing rental",
        )

    class RenterNotFound(
        renterId: UInt,
    ) : RentalError("Renter with id $renterId not found")

    class MissingCourt(
        courtId: UInt,
    ) : RentalError("Court with id $courtId not found")

    class RentalUpdateFailed(
        rentalId: UInt,
    ) : RentalError("Update for rental: $rentalId failed unexpectedly.")
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
