@file:OptIn(ExperimentalUuidApi::class)

@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.path
import pt.isel.ls.domain.Name
import pt.isel.ls.services.*
import pt.isel.ls.webapi.dto.CourtCreationInput
import pt.isel.ls.webapi.dto.CourtDetailsOutput
import pt.isel.ls.webapi.dto.toCourtsOutput
import kotlin.uuid.ExperimentalUuidApi

/**
 * This is the Court Management Api, where you can see details about a court or create one.
 */
class CourtWebApi(
    private val courtService: CourtService,
    private val userService: UserService,
) {
    fun createCourt(request: Request): Response {
        Utils.logRequest(request)
        val input = Json.decodeFromString<CourtCreationInput>(request.bodyString())
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")

        return  courtService.createCourt(Name(input.name), (input.cid))
            .fold(
                onFailure = { Response(BAD_REQUEST).body("Court already exists") },
                onSuccess = { Response(CREATED).body(Json.encodeToString(CourtDetailsOutput(it))) }
            )
    }

    fun getCourtsByClub(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val clubId = request.path("cid")?.toUIntOrNull() ?: return Response(BAD_REQUEST).body("Invalid club id")
        val limit = request.query("limit")?.toIntOrNull() ?: 10
        val skip = request.query("skip")?.toIntOrNull() ?: 0
        return courtService.getCourts(clubId, limit, skip)
            .fold(
                onFailure = { Response(NOT_FOUND).body("No courts found") },
                onSuccess = { Response(OK).body(Json.encodeToString(it.toCourtsOutput())) }
            )
    }

    fun getCourtInfo(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val courtId = request.path("crid")?.toUIntOrNull() ?: return Response(BAD_REQUEST).body("Invalid court id")
        return courtService.getCourtById(courtId)
            .fold(
                onFailure = { Response(NOT_FOUND).body("Court not found") },
                onSuccess = { Response(OK).body(Json.encodeToString(CourtDetailsOutput(it))) }
            )
    }
}
