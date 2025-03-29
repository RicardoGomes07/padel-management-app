@file:Suppress("ktlint:standard:no-empty-file", "ktlint:standard:no-wildcard-imports")

package pt.isel.ls.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import pt.isel.ls.services.*
import pt.isel.ls.webapi.dto.ProblemOutput

private fun CustomError.toResponse(): Response {
    val status =
        when (this) {
            // User Errors
            is UserError.UserAlreadyExists -> CONFLICT

            // Club Errors
            is ClubError.ClubAlreadyExists -> CONFLICT
            is ClubError.OwnerNotFound, is ClubError.ClubNotFound -> NOT_FOUND

            // Court Errors
            is CourtError.CourtNotFound -> NOT_FOUND
            is CourtError.MissingClub -> BAD_REQUEST

            // Rental Errors
            is RentalError.RentalNotFound, is RentalError.RenterNotFound, is RentalError.MissingCourt -> NOT_FOUND
            is RentalError.RentalDateInThePast -> BAD_REQUEST
            is RentalError.OverlapInTimeSlot -> CONFLICT
        }
    return Response(status).body(Json.encodeToString(ProblemOutput(this.description, this.message)))
}

fun Throwable.toResponse(): Response =
    when (this) {
        is IllegalArgumentException -> Response(BAD_REQUEST).body(message ?: "Invalid request")
        is CustomError -> this.toResponse()
        else -> Response(INTERNAL_SERVER_ERROR).body("Unexpected error: ${this.message}")
    }
