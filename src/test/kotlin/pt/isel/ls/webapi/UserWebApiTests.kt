package pt.isel.ls.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.repository.mem.TransactionManagerInMem
import pt.isel.ls.services.UserService
import pt.isel.ls.webapi.dto.UserInput
import pt.isel.ls.webapi.dto.UserOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val userApi =
    UserWebApi(
        UserService(TransactionManagerInMem()),
    )
val userRoutes =
    routes(
        "users" bind POST to userApi::createUser,
        "users/me" bind GET to userApi::getUserInfo,
    )

@OptIn(ExperimentalUuidApi::class)
fun createUser(): String {
    val name = Uuid.random().toString().take(10)
    val userResponse =
        userRoutes(
            Request(POST, "users")
                .header("Content-Type", "application/json")
                .body("""{"name":"Ric", "email":"$name@gmail.com"}"""),
        )
    val user = Json.decodeFromString<UserOutput>(userResponse.bodyString())
    return user.token
}

class UserWebApiTests {
    @Test
    fun `user creation with valid Name and Email`() {
        val response =
            userRoutes(
                Request(POST, "users")
                    .header("Content-Type", "application/json")
                    .body("""{"name":"Ric", "email":"ric@gmail.com"}"""),
            )
        assertEquals(Status.CREATED, response.status)
        val user = Json.decodeFromString<UserOutput>(response.bodyString())
        assertEquals("Ric", user.name)
    }

    @Test
    fun `user creation with duplicate Name and Email`() {
        val response =
            userRoutes(
                Request(POST, "users")
                    .header("Content-Type", "application/json")
                    .body(Json.encodeToString<UserInput>(UserInput("Riczao", "riczao@gmail.com"))),
            )
        assertEquals(Status.CREATED, response.status)

        val response1 =
            userRoutes(
                Request(POST, "users")
                    .header("Content-Type", "application/json")
                    .body(Json.encodeToString<UserInput>(UserInput("Riczao", "riczao@gmail.com"))),
            )
        assertEquals(Status.CONFLICT, response1.status)
    }

    @Test
    fun `get user info without auth`() {
        val getUsersResponse =
            userRoutes(
                Request(GET, "users/me"),
            )
        assertEquals(Status.UNAUTHORIZED, getUsersResponse.status)
    }

    @Test
    fun `get user info with auth`() {
        val token = createUser()
        val getUsersResponse =
            userRoutes(
                Request(GET, "users/me")
                    .header("Authorization", token),
            )
        assertEquals(Status.OK, getUsersResponse.status)
    }
}
