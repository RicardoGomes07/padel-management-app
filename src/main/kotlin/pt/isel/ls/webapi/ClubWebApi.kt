@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.webapi

import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.path
import pt.isel.ls.domain.Name
import pt.isel.ls.services.*
import pt.isel.ls.webapi.dto.ClubCreationInput
import pt.isel.ls.webapi.dto.ClubDetailsOutput
import pt.isel.ls.webapi.dto.toClubsOutput

/**
 * This is the Club Management Api, where you can see details about a club or create one.
 */
class ClubWebApi(
    private val clubService: ClubService,
    private val userService: UserService,
    private val rentalService: RentalService,
) {
    fun createClub(request: Request): Response {
        request.log()
        val user =
            request.validateUser(userService::validateUser)
                ?: return Response(UNAUTHORIZED).body("No Authorization")

        val input =
            validateUserInput {
                Json.decodeFromString<ClubCreationInput>(request.bodyString())
            }.getOrElse { ex -> return handleUserInputError(ex) }

        val clubName =
            validateUserInput { Name(input.name) }
                .getOrElse { ex -> return handleUserInputError(ex) }

        return clubService
            .createClub(clubName, user)
            .fold(
                onFailure = { ex -> ex.toResponse() },
                onSuccess = { Response(CREATED).body(Json.encodeToString(ClubDetailsOutput(it))) },
            )
    }

    fun getAllClubs(request: Request): Response {
        request.log()
        request.validateUser(userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")

        val limit = request.query("limit")?.toIntOrNull() ?: LIMIT_VALUE_DEFAULT
        val skip = request.query("skip")?.toIntOrNull() ?: SKIP_VALUE_DEFAULT
        return clubService
            .getClubs(limit, skip)
            .fold(
                onFailure = { ex -> ex.toResponse() },
                onSuccess = { Response(OK).body(Json.encodeToString(it.toClubsOutput())) },
            )
    }

    fun getClubInfo(request: Request): Response {
        request.log()
        request.validateUser(userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val clubId = request.path("cid")?.toUIntOrNull() ?: return Response(BAD_REQUEST).body("Invalid club id")
        return clubService
            .getClubById(clubId)
            .fold(
                onFailure = { ex -> ex.toResponse() },
                onSuccess = { Response(OK).body(Json.encodeToString(ClubDetailsOutput(it))) },
            )
    }

    fun getAvailableHours(request: Request): Response {
        request.log()
        request.validateUser(userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val courtId =
            request.path("crid")?.toUIntOrNull()
                ?: return Response(BAD_REQUEST).body("Invalid court id")
        val date =
            request
                .query("date")
                ?.let { LocalDate.parse(it) }
                ?: return Response(BAD_REQUEST).body("Invalid date")
        return rentalService
            .getAvailableHours(courtId, date)
            .fold(
                onFailure = { ex -> ex.toResponse() },
                onSuccess = { Response(OK).body(Json.encodeToString(it)) },
            )
    }
}
