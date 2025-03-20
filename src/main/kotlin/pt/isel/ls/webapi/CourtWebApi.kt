@file:OptIn(ExperimentalUuidApi::class)

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
import pt.isel.ls.repository.mem.CourtRepositoryInMem
import pt.isel.ls.repository.mem.UserRepositoryInMem
import pt.isel.ls.services.*
import pt.isel.ls.webapi.dto.CourtCreationInput
import kotlin.uuid.ExperimentalUuidApi

/**
 * This is the Court Management Api, where you can see details about a court or create one.
 */
@OptIn(ExperimentalUuidApi::class)
class CourtWebApi {
    private val courtService = CourtService(CourtRepositoryInMem)
    private val userService = UserService(UserRepositoryInMem)

    fun createCourt(request: Request): Response {
        Utils.logRequest(request)
        val input = Json.decodeFromString<CourtCreationInput>(request.bodyString())
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        return Response(CREATED).body(Json.encodeToString(courtService.createCourt(Name(input.name), (input.cid).toUInt())))
        }

    fun getCourtsByClub(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val clubId = request.path("cid")?.toUIntOrNull() ?: return Response(BAD_REQUEST).body("Invalid club id")
        return Response(OK).body(Json.encodeToString(courtService.getCourts(clubId)))
    }

    fun getCourtInfo(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val courtId = request.path("crid")?.toUIntOrNull() ?: return Response(BAD_REQUEST).body("Invalid court id")
        return when(val court = courtService.getCourtById(courtId)) {
            is Failure -> Response(NOT_FOUND).body("Court not found")
            is Success -> Response(OK).body(Json.encodeToString(court.value))
        }
    }
}
