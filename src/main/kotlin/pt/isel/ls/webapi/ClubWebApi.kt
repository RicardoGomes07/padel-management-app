@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.toName
import pt.isel.ls.services.*
import pt.isel.ls.webapi.dto.*

/**
 * This is the Club Management Api, where you can see details about a club or create one.
 */
class ClubWebApi(
    private val clubService: ClubService,
    private val userService: UserService,
) {
    fun createClub(request: Request): Response =
        request.handlerWithAuth(userService::validateUser) { user ->

            val input = Json.decodeFromString<ClubCreationInput>(request.bodyString())

            val clubName = Name(input.name)

            clubService
                .createClub(clubName, user)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(CREATED).body(Json.encodeToString(ClubDetailsOutput(it))) },
                )
        }

    fun getAllClubs(request: Request): Response =
        request.handler {
            val name = request.query("name")?.toName()

            val limit = request.query("limit")?.toIntOrNull() ?: LIMIT_VALUE_DEFAULT
            val skip = request.query("skip")?.toIntOrNull() ?: SKIP_VALUE_DEFAULT

            clubService
                .getClubs(limit, skip, name)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = {
                        Response(OK).body(
                            Json.encodeToString(
                                it.toPaginationOutput { toClubsOutput() },
                            ),
                        )
                    },
                )
        }

    fun getClubInfo(request: Request): Response =
        request.handler {
            val clubId =
                request.path("cid")?.toUIntOrNull()

            requireNotNull(clubId) { "Invalid club id" }

            clubService
                .getClubById(clubId)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(OK).body(Json.encodeToString(ClubDetailsOutput(it))) },
                )
        }
}
