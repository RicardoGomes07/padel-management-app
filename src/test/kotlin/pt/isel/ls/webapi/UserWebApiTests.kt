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
import pt.isel.ls.webapi.dto.UserOutput
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

val userApi =
    UserWebApi(
        UserService(TransactionManagerInMem()),
    )
val userRoutes =
    routes(
        "users" bind POST to userApi::createUser,
        "users/me" bind GET to userApi::getUserInfo,
    )

fun createUser(): String {
    val name = UUID.randomUUID().toString().take(5)
    println(name)
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
        assertEquals(response.status, Status.CREATED)
        val user = Json.decodeFromString<UserOutput>(response.bodyString())
        println(user)
        assertEquals(user.name, "Ric")
    }

    @Test
    fun `user creation with duplicate Name and Email`() {
        val response =
            userRoutes(
                Request(POST, "users")
                    .header("Content-Type", "application/json")
                    .body("""{"name":"Riczao", "email":"riczao@gmail.com"}"""),
            )
        assertEquals(response.status, Status.CREATED)

        val response1 =
            userRoutes(
                Request(POST, "users")
                    .header("Content-Type", "application/json")
                    .body("""{"name":"Riczao", "email":"riczao@gmail.com"}"""),
            )
        assertEquals(response1.status, Status.BAD_REQUEST)
    }

    @Test
    fun `get user info without auth`() {
        val getUsersResponse =
            userRoutes(
                Request(GET, "users/me"),
            )
        assertEquals(Status.UNAUTHORIZED, getUsersResponse.status)
        println(getUsersResponse.bodyString())
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
        println(getUsersResponse.bodyString())
    }
}
