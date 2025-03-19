package pt.isel.ls.webApi

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import pt.isel.ls.webApi.dto.UserInput

/**
 * This is the User Management Api, where you can see details about a user or create one.
 */

class UserWebApi {
    // private val userService = TODO()

    fun createUser(request: Request): Response {
        Utils.logRequest(request)
        val user = Json.decodeFromString<UserInput>(request.bodyString())
        // userService.::::(user)
        return Response(CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(user))
    }

    fun getUserInfo(request: Request): Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getUserRentals(request: Request): Response {
        Utils.logRequest(request)
        return TODO()
    }
}
