@file:Suppress("ktlint:standard:no-wildcard-imports")

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
import org.http4k.core.Status.Companion.UNPROCESSABLE_ENTITY
import org.http4k.routing.path
import pt.isel.ls.domain.Name
import pt.isel.ls.services.*
import pt.isel.ls.webapi.dto.ClubCreationInput
import pt.isel.ls.webapi.dto.ClubDetailsOutput
import pt.isel.ls.webapi.dto.toClubsOutput

/**
 * This is the Club Management Api, where you can see details about a club or create one.
 */
class ClubWebApi(
    private val clubService: ClubService,
    private val userService: UserService,
    private val rentalService: RentalService,
) {
    fun createClub(request: Request): Response {
        Utils.logRequest(request)
        val input = Json.decodeFromString<ClubCreationInput>(request.bodyString())
        val user =
            Utils.verifyAndValidateUser(request, userService::validateUser)
                ?: return Response(UNAUTHORIZED).body("No Authorization")
        return clubService.createClub(Name(input.name), user)
            .fold(
              onFailure =  { ex ->
                  when(ex) {
                    is IllegalStateException -> Response(NOT_FOUND).body(ex.message!!)
                    is IllegalArgumentException -> Response(BAD_REQUEST).body(ex.message!!)
                    else -> Response(UNPROCESSABLE_ENTITY).body(ex.message!!)
                  }
               },
              onSuccess =  { Response(CREATED).body(Json.encodeToString(ClubDetailsOutput(it))) }
            )
    }

    fun getAllClubs(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val limit = request.query("limit")?.toIntOrNull() ?: 10
        val skip = request.query("skip")?.toIntOrNull() ?: 0
        return clubService.getClubs(limit, skip)
            .fold(
              onFailure =  { ex ->
                  when(ex) {
                      is IllegalStateException -> Response(NOT_FOUND).body(ex.message!!)
                        is IllegalArgumentException -> Response(BAD_REQUEST).body(ex.message!!)
                        else -> Response(UNPROCESSABLE_ENTITY).body(ex.message!!)
                  }
               },
              onSuccess =  { Response(OK).body(Json.encodeToString(it.toClubsOutput())) }
            )
    }

    fun getClubInfo(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val clubId = request.path("cid")?.toUIntOrNull() ?: return Response(BAD_REQUEST).body("Invalid club id")
        return clubService.getClubById(clubId)
            .fold(
              onFailure =  { ex ->
                  when(ex){
                      is IllegalStateException -> Response(NOT_FOUND).body(ex.message!!)
                      is IllegalArgumentException -> Response(BAD_REQUEST).body(ex.message!!)
                      else -> Response(UNPROCESSABLE_ENTITY).body(ex.message!!)
                  }
               },
              onSuccess =  { Response(OK).body(Json.encodeToString(ClubDetailsOutput(it))) }
            )
    }

    fun getAvailableHours(request: Request): Response {
        Utils.logRequest(request)
        Utils.verifyAndValidateUser(request, userService::validateUser)
            ?: return Response(UNAUTHORIZED).body("No Authorization")
        val courtId =
            request.path("crid")?.toUIntOrNull()
                ?: return Response(BAD_REQUEST).body("Invalid court id")
        val date =
            request
                .query("date")
                ?.let { LocalDate.parse(it) }
                ?: return Response(BAD_REQUEST).body("Invalid date")
        return rentalService.getAvailableHours(courtId, date)
            .fold(
              onFailure =  { ex->
                  when(ex){
                      is IllegalStateException -> Response(NOT_FOUND).body(ex.message!!)
                      is IllegalArgumentException -> Response(BAD_REQUEST).body(ex.message!!)
                      else -> Response(UNPROCESSABLE_ENTITY).body(ex.message!!)
                  }
               },
              onSuccess =  { Response(OK).body(Json.encodeToString(it)) }
            )
    }
}
