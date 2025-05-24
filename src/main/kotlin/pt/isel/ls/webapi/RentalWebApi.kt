@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.webapi

import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import pt.isel.ls.domain.TimeSlot
import pt.isel.ls.services.*
import pt.isel.ls.webapi.dto.AvailableHoursInput
import pt.isel.ls.webapi.dto.DateAndRentTimeInput
import pt.isel.ls.webapi.dto.RentalCreationInput
import pt.isel.ls.webapi.dto.RentalDetailsOutput
import pt.isel.ls.webapi.dto.RentalUpdateInput
import pt.isel.ls.webapi.dto.toAvailableHours
import pt.isel.ls.webapi.dto.toCourtsOutput
import pt.isel.ls.webapi.dto.toPaginationOutput
import pt.isel.ls.webapi.dto.toRentalsOutput

/**
 * This is the Rental Management Api, where you can see details about a rental or create one.
 */
class RentalWebApi(
    private val rentalService: RentalService,
    private val userService: UserService,
) {
    fun createRental(request: Request): Response =
        request.handlerWithAuth(userService::validateUser) { user ->
            val input = Json.decodeFromString<RentalCreationInput>(request.bodyString())
            print("Entrei no createRental")
            val timeSlot = TimeSlot(input.initialHour, input.finalHour)
            val courtId = request.path("crid")?.toUIntOrNull()
            requireNotNull(courtId) { "Invalid court id" }
            val clubId = request.path("cid")?.toUIntOrNull()
            requireNotNull(clubId) { "Invalid club id" }

            rentalService
                .createRental(
                    input.date,
                    timeSlot,
                    user.uid,
                    courtId,
                ).fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(CREATED).body(Json.encodeToString(RentalDetailsOutput(it))) },
                )
        }

    fun getRentalsOnCourt(request: Request): Response =
        request.handler {
            val courtId = request.path("crid")?.toUIntOrNull()

            requireNotNull(courtId) { "Invalid court id" }

            val date = request.query("date")?.let { LocalDate.parse(it) }
            val limit = request.query("limit")?.toIntOrNull() ?: LIMIT_VALUE_DEFAULT
            val skip = request.query("skip")?.toIntOrNull() ?: SKIP_VALUE_DEFAULT

            rentalService
                .getRentals(courtId, date, limit, skip)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = {
                        Response(OK).body(
                            Json.encodeToString(it.toPaginationOutput { toRentalsOutput() }),
                        )
                    },
                )
        }

    fun getUserRentals(request: Request): Response =
        request.handler {
            val limit = request.query("limit")?.toIntOrNull() ?: LIMIT_VALUE_DEFAULT
            val skip = request.query("skip")?.toIntOrNull() ?: SKIP_VALUE_DEFAULT

            val userId = request.path("uid")?.toUIntOrNull()
            requireNotNull(userId) { "Invalid user id" }

            rentalService
                .getUserRentals(userId, limit, skip)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = {
                        Response(OK).body(
                            Json.encodeToString(it.toPaginationOutput { toRentalsOutput() }),
                        )
                    },
                )
        }

    fun getRentalInfo(request: Request): Response =
        request.handler {
            val rentalId = request.path("rid")?.toUIntOrNull()

            requireNotNull(rentalId) { "Invalid rental id" }

            rentalService
                .getRentalById(rentalId)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(OK).body(Json.encodeToString(RentalDetailsOutput(it))) },
                )
        }

    fun getAvailableHours(request: Request): Response =
        request.handler {
            val courtId = request.path("crid")?.toUIntOrNull()

            requireNotNull(courtId) { "Invalid court id" }

            val date = Json.decodeFromString<AvailableHoursInput>(request.bodyString()).date

            require(date >= currentDate()) { "Date must not be in the past" }
            val currHour = currentHour()
            rentalService
                .getAvailableHours(courtId, date)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = {
                        val hours = if (date == currentDate()) it.filter { hour -> hour > currHour.toUInt() } else it
                        Response(OK)
                            .body(Json.encodeToString(hours.toAvailableHours()))
                    },
                )
        }

    fun getAvailableCourtsByDateAndRentTime(request: Request): Response =
        request.handler {
            val clubId = request.path("cid")?.toUIntOrNull()
            requireNotNull(clubId) { "Invalid club id" }

            val input = Json.decodeFromString<DateAndRentTimeInput>(request.bodyString())

            require(input.date >= currentDate()) { "Date must not be in the past" }

            val timeSlot = TimeSlot(input.initialHour, input.finalHour)

            rentalService
                .getAvailableCourtsByDateAndRentTime(clubId, input.date, timeSlot)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = {
                        Response(OK).body(
                            Json.encodeToString(it.toPaginationOutput { toCourtsOutput() }),
                        )
                    },
                )
        }

    fun deleteRental(request: Request): Response =
        request.handlerWithAuth(userService::validateUser) {
            val rentalId = request.path("rid")?.toUIntOrNull()

            requireNotNull(rentalId)

            rentalService
                .deleteRental(rentalId)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(OK) },
                )
        }

    fun updateRental(request: Request): Response =
        request.handlerWithAuth(userService::validateUser) {
            val courtId = request.path("crid")?.toUIntOrNull()
            val rentalId = request.path("rid")?.toUIntOrNull()

            requireNotNull(rentalId) { "Missing Rental Id" }
            requireNotNull(courtId) { "Missing Court Id" }

            val input = Json.decodeFromString<RentalUpdateInput>(request.bodyString())

            val rentTime = TimeSlot(input.initialHour.toUInt(), input.finalHour.toUInt())

            rentalService
                .updateDateAndRentTime(
                    rentalId,
                    courtId,
                    input.date,
                    rentTime,
                ).fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(OK).body(Json.encodeToString(RentalDetailsOutput(it))) },
                )
        }
}
