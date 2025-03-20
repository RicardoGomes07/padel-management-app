package pt.isel.ls.webapi

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.path
import pt.isel.ls.repository.mem.ClubRepositoryInMem
import pt.isel.ls.repository.mem.RentalRepositoryInMem
import pt.isel.ls.repository.mem.UserRepositoryInMem
import pt.isel.ls.services.ClubService
import pt.isel.ls.services.Either
import pt.isel.ls.services.RentalService
import pt.isel.ls.services.UserService
import pt.isel.ls.webapi.dto.ClubCreationInput
import kotlin.uuid.ExperimentalUuidApi

/**
 * This is the Club Management Api, where you can see details about a club or create one.
 */
@OptIn(ExperimentalUuidApi::class)
class ClubWebApi {
    private val clubService = ClubService(ClubRepositoryInMem)
    private val userService = UserService(UserRepositoryInMem)
    private val rentalService = RentalService(RentalRepositoryInMem)

    fun createClub(request: Request): Response {
        Utils.logRequest(request)
        val input = Json.decodeFromString<ClubCreationInput>(request.bodyString())
        val user = Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        return when(val club = clubService.createClub(input.name, user)) {
            is Either.Left -> Response(BAD_REQUEST).body("Club already exists")
            is Either.Right -> Response(CREATED).body(Json.encodeToString(club.value))
        }
    }

    fun getAllClubs(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        return Response(OK).body(Json.encodeToString(clubService.getClubs()))
    }

    fun getClubInfo(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val clubId = request.path("cid")?.toUIntOrNull() ?: return Response(BAD_REQUEST).body("Invalid club id")
        return when (val club = clubService.getClubById(clubId)) {
            is Either.Left -> Response(NOT_FOUND).body("Club not found")
            is Either.Right -> Response(OK).body(Json.encodeToString(club.value))
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun getAvailableHours(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val courtId = request.path("crid")?.toUIntOrNull()
            ?: return Response(BAD_REQUEST).body("Invalid court id")
        val date = request.query("date")
            ?.let { LocalDateTime.parse(it) }
            ?: return Response(BAD_REQUEST).body("Invalid date")
        return Response(OK).body(Json.encodeToString(rentalService.getAvailableHours(courtId,date)))
    }
}
