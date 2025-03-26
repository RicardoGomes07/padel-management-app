@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.webapi

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
import pt.isel.ls.webapi.dto.CourtCreationInput
import pt.isel.ls.webapi.dto.CourtDetailsOutput
import pt.isel.ls.webapi.dto.toCourtsOutput

/**
 * This is the Court Management Api, where you can see details about a court or create one.
 */
class CourtWebApi(
    private val courtService: CourtService,
    private val userService: UserService,
) {
    fun createCourt(request: Request): Response {
        request.log()
        request.validateUser(userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")

        val input =
            validateUserInput {
                Json.decodeFromString<CourtCreationInput>(request.bodyString())
            }.getOrElse { ex -> return handleUserInputError(ex) }

        val courtName =
            validateUserInput { Name(input.name) }
                .getOrElse { ex -> return handleUserInputError(ex) }

        return courtService
            .createCourt(courtName, input.cid)
            .fold(
                onFailure = { ex -> ex.toResponse() },
                onSuccess = { Response(CREATED).body(Json.encodeToString(CourtDetailsOutput(it))) },
            )
    }

    fun getCourtsByClub(request: Request): Response {
        request.log()
        request.validateUser(userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")

        val clubId =
            request.path("cid")?.toUIntOrNull()
                ?: return Response(BAD_REQUEST).body("Invalid club id")
        val limit = request.query("limit")?.toIntOrNull() ?: LIMIT_VALUE_DEFAULT
        val skip = request.query("skip")?.toIntOrNull() ?: SKIP_VALUE_DEFAULT

        return courtService
            .getCourts(clubId, limit, skip)
            .fold(
                onFailure = { ex -> ex.toResponse() },
                onSuccess = { Response(OK).body(Json.encodeToString(it.toCourtsOutput())) },
            )
    }

    fun getCourtInfo(request: Request): Response {
        request.log()
        request.validateUser(userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")

        val courtId =
            request.path("crid")?.toUIntOrNull()
                ?: return Response(BAD_REQUEST).body("Invalid court id")

        return courtService
            .getCourtById(courtId)
            .fold(
                onFailure = { ex -> ex.toResponse() },
                onSuccess = { Response(OK).body(Json.encodeToString(CourtDetailsOutput(it))) },
            )
    }
}
