@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.services.*
import pt.isel.ls.webapi.dto.*

/**
 * This is the User Management Api, where you can see details about a user or create one.
 */
class UserWebApi(
    private val userService: UserService,
) {
    fun createUser(request: Request): Response =
        request.handler {
            val input = Json.decodeFromString<UserInput>(request.bodyString())
            val userName = Name(input.name)
            val email = Email(input.email)
            userService
                .createUser(userName, email)
                .fold(
                    onFailure = { ex -> ex.toResponse() },
                    onSuccess = { Response(CREATED).body(Json.encodeToString(UserOutput(it))) },
                )
        }

    fun getUserInfo(request: Request): Response =
        request.handler {
            val userId = request.path("uid")?.toUIntOrNull()
            requireNotNull(userId) { "Invalid user id" }

            val user = userService.findUserById(userId)
            requireNotNull(user)

            Response(OK).body(Json.encodeToString(UserDetails(user)))
        }
}
