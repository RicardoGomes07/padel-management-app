package pt.isel.ls.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.repository.mem.RentalRepositoryInMem
import pt.isel.ls.repository.mem.UserRepositoryInMem
import pt.isel.ls.services.Either
import pt.isel.ls.services.RentalService
import pt.isel.ls.services.UserService
import pt.isel.ls.webapi.dto.UserInput
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * This is the User Management Api, where you can see details about a user or create one.
 */

class UserWebApi {
    private val userService = UserService(UserRepositoryInMem)
    private val rentalService = RentalService(RentalRepositoryInMem)

    fun createUser(request: Request): Response {
        Utils.logRequest(request)
        val input = Json.decodeFromString<UserInput>(request.bodyString())
        return when(val user = userService.createUser(Name(input.name), Email(input.email))) {
            is Either.Left -> Response(BAD_REQUEST).body("User already exists")
            is Either.Right -> Response(CREATED).body(Json.encodeToString(user.value))
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun getUserInfo(request: Request): Response {
        Utils.logRequest(request)
        val userInfo = Utils.verifyToken(request)
            ?.let {token -> Uuid.parse(token) }
            ?.let {userToken -> userService.validateUser(userToken) }
            ?: return Response(Status.UNAUTHORIZED).body("No Authorization")
        return Response(Status.OK).body(Json.encodeToString(userInfo))

    }

    @OptIn(ExperimentalUuidApi::class)
    fun getUserRentals(request: Request): Response {
        Utils.logRequest(request)
        val userInfo = Utils.verifyToken(request)
            ?.let {token -> Uuid.parse(token) }
            ?.let {userToken -> userService.validateUser(userToken) }
            ?: return Response(Status.UNAUTHORIZED).body("No Authorization")
        return when (val rentals = rentalService.getUserRentals(userInfo.uid)) {
            is Either.Left -> Response(Status.NOT_FOUND).body("No rentals found")
            is Either.Right -> Response(Status.OK).body(Json.encodeToString(rentals.value))
        }
    }
}
