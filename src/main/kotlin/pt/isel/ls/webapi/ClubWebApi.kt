package pt.isel.ls.webapi

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import pt.isel.ls.domain.User
import pt.isel.ls.repository.mem.ClubRepositoryInMem
import pt.isel.ls.repository.mem.RentalRepositoryInMem
import pt.isel.ls.repository.mem.UserRepositoryInMem
import pt.isel.ls.services.ClubService
import pt.isel.ls.services.Either
import pt.isel.ls.services.RentalService
import pt.isel.ls.services.UserService
import pt.isel.ls.webapi.dto.ClubCreationInput
import pt.isel.ls.webapi.dto.OwnerOutput
import pt.isel.ls.webapi.dto.UserInput
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
        val user = Utils.verifyToken(request)
            ?.let { token -> Uuid.parse(token) }
            ?.let { userToken -> userService.validateUser(userToken) }
            ?: return Response(Status.UNAUTHORIZED).body("No Authorization")
        return when(val club = clubService.createClub(input.name, user)) {
            is Either.Left -> Response(Status.BAD_REQUEST).body("Club already exists")
            is Either.Right -> Response(Status.CREATED).body(Json.encodeToString(club.value))
        }
    }

    fun getAllClubs(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyToken(request)
            ?.let { token -> Uuid.parse(token) }
            ?.let { userToken -> userService.validateUser(userToken) }
            ?: return Response(Status.UNAUTHORIZED).body("No Authorization")
        return Response(Status.OK).body(Json.encodeToString(clubService.getClubs()))
    }

    fun getClubInfo(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyToken(request)
            ?.let { token -> Uuid.parse(token) }
            ?.let { userToken -> userService.validateUser(userToken) }
            ?: return Response(Status.UNAUTHORIZED).body("No Authorization")
        val clubId = request.path("cid")?.toUIntOrNull() ?: return Response(Status.BAD_REQUEST).body("Invalid club id")
        return when (val club = clubService.getClubById(clubId)) {
            is Either.Left -> Response(Status.NOT_FOUND).body("Club not found")
            is Either.Right -> Response(Status.OK).body(Json.encodeToString(club.value))
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun getAvailableHours(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyToken(request)
            ?.let { token -> Uuid.parse(token) }
            ?.let { userToken -> userService.validateUser(userToken) }
            ?: return Response(Status.UNAUTHORIZED).body("No Authorization")
        val courtId = request.path("crid")?.toUIntOrNull() ?: return Response(Status.BAD_REQUEST).body("Invalid court id")
        val date = request.query("date")?.let { LocalDateTime.parse(it) } ?: return Response(Status.BAD_REQUEST).body("Invalid date")
        return Response(Status.OK).body(Json.encodeToString(rentalService.getAvailableHours(courtId,date)))
    }
}
