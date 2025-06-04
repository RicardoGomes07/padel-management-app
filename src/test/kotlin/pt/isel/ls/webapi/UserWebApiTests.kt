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
import pt.isel.ls.webapi.dto.AuthUser
import pt.isel.ls.webapi.dto.LoginInput
import pt.isel.ls.webapi.dto.UserCreationInput
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
        "users/login" bind POST to userApi::login,
        "users/logout" bind POST to userApi::logout,
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
    val email = "$name@email.com"
    val password = "password"
    userRoutes(
        Request(POST, "users")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString<UserCreationInput>(UserCreationInput("Ric", email, password))),
    )

    val loginResponse =
        userRoutes(
            Request(POST, "users/login")
                .header("Content-Type", "application/json")
                .body(Json.encodeToString<LoginInput>(LoginInput(email, password))),
        )

    val loggedIn = Json.decodeFromString<AuthUser>(loginResponse.bodyString())

    return loggedIn.token
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
                    .body(Json.encodeToString<UserCreationInput>(UserCreationInput("Ric", "ric@email.com", "password"))),
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
                    .body(Json.encodeToString<UserCreationInput>(UserCreationInput("Riczao", "riczao@gmail.com", "password"))),
            )
        assertEquals(Status.CREATED, response.status)

        val response1 =
            userRoutes(
                Request(POST, "users")
                    .header("Content-Type", "application/json")
                    .body(Json.encodeToString<UserCreationInput>(UserCreationInput("Riczao", "riczao@gmail.com", "password"))),
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
                    .body(Json.encodeToString<UserCreationInput>(UserCreationInput("Ric", "$name@email.com", "password"))),
            )

        assertEquals(Status.CREATED, userResponse.status)
    }

    @Test
    fun `login a user`() {
        val email = "ric@email.com"
        val password = "password"
        val createResponse =
            userRoutes(
                Request(POST, "users")
                    .header("Content-Type", "application/json")
                    .body(Json.encodeToString<UserCreationInput>(UserCreationInput("Ric", email, password))),
            )
        assertEquals(Status.CREATED, createResponse.status)

        val loginResponse =
            userRoutes(
                Request(POST, "users/login")
                    .header("Content-Type", "application/json")
                    .body(Json.encodeToString<LoginInput>(LoginInput(email, password))),
            )
        assertEquals(Status.OK, loginResponse.status)
    }

    @Test
    fun `login and logout a user`() {
        val email = "ric@email.com"
        val password = "password"
        val createResponse =
            userRoutes(
                Request(POST, "users")
                    .header("Content-Type", "application/json")
                    .body(Json.encodeToString<UserCreationInput>(UserCreationInput("Ric", email, password))),
            )
        assertEquals(Status.CREATED, createResponse.status)

        val loginResponse =
            userRoutes(
                Request(POST, "users/login")
                    .header("Content-Type", "application/json")
                    .body(Json.encodeToString<LoginInput>(LoginInput(email, password))),
            )
        assertEquals(Status.OK, loginResponse.status)

        val loggedIn = Json.decodeFromString<AuthUser>(loginResponse.body.toString())

        val logoutResponse =
            userRoutes(
                Request(POST, "users/logout")
                    .header("Authorization", loggedIn.token),
            )
        assertEquals(Status.OK, logoutResponse.status)
    }
}
