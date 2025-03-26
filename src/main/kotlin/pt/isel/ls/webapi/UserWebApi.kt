@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
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
    fun createUser(request: Request): Response {
        request.log()
        val input =
            validateUserInput {
                Json.decodeFromString<UserInput>(request.bodyString())
            }.getOrElse { ex -> return handleUserInputError(ex) }
        val userName =
            validateUserInput { Name(input.name) }
                .getOrElse { ex -> return handleUserInputError(ex) }
        val email =
            validateUserInput { Email(input.email) }
                .getOrElse { ex -> return handleUserInputError(ex) }
        return userService
            .createUser(userName, email)
            .fold(
                onFailure = { ex -> ex.toResponse() },
                onSuccess = { Response(CREATED).body(Json.encodeToString(UserOutput(it))) },
            )
    }

    fun getUserInfo(request: Request): Response {
        request.log()
        val userInfo =
            request.validateUser(userService::validateUser)
                ?: return Response(UNAUTHORIZED).body("No Authorization")
        return Response(OK).body(Json.encodeToString(UserOutput(userInfo)))
    }
}
