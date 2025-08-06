@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import pt.isel.ls.domain.toName
import pt.isel.ls.services.*
import pt.isel.ls.webapi.dto.CourtCreationInput
import pt.isel.ls.webapi.dto.CourtDetailsOutput
import pt.isel.ls.webapi.dto.CourtOutput
import pt.isel.ls.webapi.dto.toCourtsOutput

/**
 * This is the Court Management Api, where you can see details about a court or create one.
 */
class CourtWebApi(
    private val courtService: CourtService,
    private val userService: UserService,
) {
    fun createCourt(request: Request): Response =
        request.handlerWithAuth(userService::validateUser) {
            val courtName = Json.decodeFromString<CourtCreationInput>(request.bodyString()).name.toName()

            val clubId = request.path("cid")?.toUIntOrNull()
            requireNotNull(clubId) { "Invalid club id" }

            courtService
                .createCourt(courtName, clubId)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(CREATED).body(Json.encodeToString(CourtDetailsOutput(it))) },
                )
        }

    fun getCourtsByClub(request: Request): Response =
        request.handler {
            val clubId = request.path("cid")?.toUIntOrNull()

            requireNotNull(clubId) { "Invalid club id" }

            val limit = request.query("limit")?.toIntOrNull() ?: LIMIT_VALUE_DEFAULT
            val skip = request.query("skip")?.toIntOrNull() ?: SKIP_VALUE_DEFAULT

            courtService
                .getCourts(clubId, limit, skip)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(OK).body(Json.encodeToString(it.toCourtsOutput())) },
                )
        }

    fun getCourtInfo(request: Request): Response =
        request.handler {
            val courtId = request.path("crid")?.toUIntOrNull()

            requireNotNull(courtId) { "Invalid court id" }

            courtService
                .getCourtById(courtId)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(OK).body(Json.encodeToString(CourtOutput(it))) },
                )
        }
}
