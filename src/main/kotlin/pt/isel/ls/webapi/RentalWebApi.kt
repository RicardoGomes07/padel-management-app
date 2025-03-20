package pt.isel.ls.webapi

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.path
import pt.isel.ls.repository.mem.RentalRepositoryInMem
import pt.isel.ls.repository.mem.UserRepositoryInMem
import pt.isel.ls.services.Failure
import pt.isel.ls.services.RentalService
import pt.isel.ls.services.Success
import pt.isel.ls.services.UserService
import pt.isel.ls.webapi.dto.RentalCreationInput
import kotlin.uuid.ExperimentalUuidApi

/**
 * This is the Rental Management Api, where you can see details about a rental or create one.
 */
@OptIn(ExperimentalUuidApi::class)
class RentalWebApi {
    private val rentalService = RentalService(RentalRepositoryInMem)
    private val userService = UserService(UserRepositoryInMem)

    fun createRental(request: Request): Response {
        Utils.logRequest(request)
        val input = Json.decodeFromString<RentalCreationInput>(request.bodyString())
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        return Response(CREATED).body(Json.encodeToString(rentalService.createRental(input.date, input.duration, (input.cid).toUInt(), (input.crid).toUInt())))
    }

    fun getAllRentals(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val courtId = request.path("crid")?.toUIntOrNull() ?: return Response(BAD_REQUEST).body("Invalid court id")
        val date = request.query("date")?.let { LocalDateTime.parse(it) }
        return Response(OK).body(Json.encodeToString(rentalService.getRentals(courtId, date)))
    }

    fun getRentalInfo(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val rentalId = request.path("rid")?.toUIntOrNull() ?: return Response(BAD_REQUEST).body("Invalid rental id")
        return when(val rentalInfo = rentalService.getRentalById(rentalId)) {
            is Failure -> Response(BAD_REQUEST).body("Rental not found")
            is Success -> Response(OK).body(Json.encodeToString(rentalInfo.value))
        }
    }
}
