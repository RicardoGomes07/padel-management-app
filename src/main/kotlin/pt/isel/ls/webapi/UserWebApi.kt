@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.services.*
import pt.isel.ls.webapi.dto.*
import kotlin.uuid.ExperimentalUuidApi

/**
 * This is the User Management Api, where you can see details about a user or create one.
 */
@OptIn(ExperimentalUuidApi::class)
class UserWebApi(
    private val userService: UserService,
) {
    fun createUser(request: Request): Response {
        Utils.logRequest(request)
        val input = Json.decodeFromString<UserInput>(request.bodyString())
        return when (val user = userService.createUser(Name(input.name), Email(input.email))) {
            is Failure -> Response(BAD_REQUEST).body("User already exists")
            is Success -> Response(CREATED).body(Json.encodeToString(UserOutput(user.value)))
        }
    }

    fun getUserInfo(request: Request): Response {
        Utils.logRequest(request)
        val userInfo =
            Utils.verifyAndValidateUser(request, userService::validateUser)
                ?: return Response(UNAUTHORIZED).body("No Authorization")
        return Response(OK).body(Json.encodeToString(UserOutput(userInfo)))
    }
}
