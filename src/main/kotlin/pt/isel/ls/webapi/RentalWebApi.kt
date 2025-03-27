package pt.isel.ls.webapi

import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
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
    fun createRental(request: Request): Response =
        request.handlerWithAuth(userService::validateUser) {
            val input = Json.decodeFromString<RentalCreationInput>(request.bodyString())

            val timeSlot = TimeSlot(input.initialHour.toUInt(), input.finalHour.toUInt())

            rentalService
                .createRental(
                    input.date,
                    timeSlot,
                    input.cid.toUInt(),
                    input.crid.toUInt(),
                ).fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(CREATED).body(Json.encodeToString(RentalDetailsOutput(it))) },
                )
        }

    fun getAllRentals(request: Request): Response =
        request.handlerWithAuth(userService::validateUser) {
            val courtId = request.path("crid")?.toUIntOrNull()

            requireNotNull(courtId) { "Invalid court id" }

            val date = request.query("date")?.let { LocalDate.parse(it) }
            val limit = request.query("limit")?.toIntOrNull() ?: LIMIT_VALUE_DEFAULT
            val skip = request.query("skip")?.toIntOrNull() ?: SKIP_VALUE_DEFAULT

            rentalService
                .getRentals(courtId, date, limit, skip)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(OK).body(Json.encodeToString(it.toRentalsOutput())) },
                )
        }

    fun getUserRentals(request: Request): Response =
        request.handlerWithAuth(userService::validateUser) { user ->
            val limit = request.query("limit")?.toIntOrNull() ?: LIMIT_VALUE_DEFAULT
            val skip = request.query("skip")?.toIntOrNull() ?: SKIP_VALUE_DEFAULT

            rentalService
                .getUserRentals(user.uid, limit, skip)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(OK).body(Json.encodeToString(it.toRentalsOutput())) },
                )
        }

    fun getRentalInfo(request: Request): Response =
        request.handlerWithAuth(userService::validateUser) {
            val rentalId = request.path("rid")?.toUIntOrNull()

            requireNotNull(rentalId) { "Invalid rental id" }

            rentalService
                .getRentalById(rentalId)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(OK).body(Json.encodeToString(RentalDetailsOutput(it))) },
                )
        }
}
