package pt.isel.ls.webapi

import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.path
import pt.isel.ls.domain.TimeSlot
import pt.isel.ls.services.RentalService
import pt.isel.ls.services.UserService
import pt.isel.ls.webapi.dto.RentalCreationInput
import pt.isel.ls.webapi.dto.RentalDetailsOutput
import pt.isel.ls.webapi.dto.toRentalsOutput

/**
 * This is the Rental Management Api, where you can see details about a rental or create one.
 */
class RentalWebApi(
    private val rentalService: RentalService,
    private val userService: UserService,
) {
    fun createRental(request: Request): Response {
        Utils.logRequest(request)
        val input = Json.decodeFromString<RentalCreationInput>(request.bodyString())
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        return rentalService.createRental(input.date, TimeSlot(input.initialHour.toUInt(), input.finalHour.toUInt()), input.cid.toUInt(), input.crid.toUInt())
            .fold(
                onFailure = { Response(BAD_REQUEST).body("Rental already exists") },
                onSuccess = { Response(CREATED).body(Json.encodeToString(RentalDetailsOutput(it))) }
            )
    }

    fun getAllRentals(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val courtId = request.path("crid")?.toUIntOrNull() ?: return Response(BAD_REQUEST).body("Invalid court id")
        val date = request.query("date")?.let { LocalDate.parse(it) }
        val limit = request.query("limit")?.toIntOrNull() ?: 10
        val skip = request.query("skip")?.toIntOrNull() ?: 0
        return rentalService.getRentals(courtId, date, limit, skip)
            .fold(
                onFailure = { Response(NOT_FOUND).body("Rentals not found") },
                onSuccess = { Response(OK).body(Json.encodeToString(it.toRentalsOutput())) }
            )
    }

    fun getUserRentals(request: Request): Response {
        Utils.logRequest(request)
        val userInfo =
            Utils.verifyAndValidateUser(request, userService::validateUser)
                ?: return Response(UNAUTHORIZED).body("No Authorization")
        val limit = request.query("limit")?.toIntOrNull() ?: 10
        val skip = request.query("skip")?.toIntOrNull() ?: 0
        return rentalService.getUserRentals(userInfo.uid, limit, skip)
            .fold(
                onFailure = { Response(NOT_FOUND).body("Rentals not found") },
                onSuccess = { Response(OK).body(Json.encodeToString(it.toRentalsOutput())) }
            )
    }

    fun getRentalInfo(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val rentalId = request.path("rid")?.toUIntOrNull() ?: return Response(BAD_REQUEST).body("Invalid rental id")
        return rentalService.getRentalById(rentalId)
            .fold(
                onFailure = { Response(NOT_FOUND).body("Rental not found") },
                onSuccess = { Response(OK).body(Json.encodeToString(RentalDetailsOutput(it))) }
            )
    }
}
