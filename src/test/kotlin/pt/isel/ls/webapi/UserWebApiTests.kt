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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private val transactionManager = TransactionManagerInMem()

val userApi =
    UserWebApi(
        UserService(transactionManager),
    )
val userRoutes =
    routes(
        "users" bind POST to userApi::createUser,
        "users/{uid}" bind GET to userApi::getUserInfo,
    )

fun randomString(
    length: Int,
    charset: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
): String =
    (1..length)
        .map { charset.random() }
        .joinToString("")

fun createUser(): String {
    val name = randomString(10)
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
    @BeforeTest
    fun setup() {
        transactionManager.run {
            it.userRepo.clear()
        }
    }

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
        assertEquals(Status.BAD_REQUEST, response1.status)
    }

    @Test
    fun `get user info with auth`() {
        val name = randomString(10)
        val userResponse =
            userRoutes(
                Request(POST, "users")
                    .header("Content-Type", "application/json")
                    .body("""{"name":"Ric", "email":"$name@gmail.com"}"""),
            )

        assertEquals(Status.CREATED, userResponse.status)
    }
}
