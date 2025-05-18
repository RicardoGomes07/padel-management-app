package pt.isel.ls.webapi

import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import pt.isel.ls.domain.toEmail
import pt.isel.ls.domain.toName
import pt.isel.ls.repository.mem.TransactionManagerInMem
import pt.isel.ls.services.UserError
import pt.isel.ls.services.UserService
import pt.isel.ls.webapi.dto.AvailableHours
import pt.isel.ls.webapi.dto.ProblemOutput
import pt.isel.ls.webapi.dto.UIntInterval
import pt.isel.ls.webapi.dto.toAvailableHours
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UtilsTests {
    private val userService = UserService(TransactionManagerInMem())

    @Test
    fun `if exception is thrown inside the handler, it returns an response with the error`() {
        val request = Request(GET, "/")
        val internalError =
            request.handler {
                throw Exception("Test exception")
            }
        assertEquals(500, internalError.status.code)
        val (title, description) = ProblemOutput("Unexpected error", "Test exception")
        assertTrue(internalError.bodyString().contains(title))
        assertTrue(internalError.bodyString().contains(description))

        val illegalArgError =
            request.handler {
                throw IllegalArgumentException("Invalid request")
            }
        assertEquals(400, illegalArgError.status.code)
        assertTrue(illegalArgError.bodyString().contains("Invalid request"))

        val customError =
            request.handler {
                throw UserError.UserAlreadyExists("User already exists")
            }
        assertEquals(400, customError.status.code)
        val probOut =
            ProblemOutput("Error during user operation", "User already exists")
        assertTrue(customError.bodyString().contains(probOut.title))
        assertTrue(customError.bodyString().contains(probOut.description))
    }

    @Test
    fun `handler with authentication`() {
        val request = Request(GET, "/")
        val responseWithUnauthUser =
            request
                .handlerWithAuth(userService::validateUser) { _ -> Response(Status.OK) }
        assertEquals(401, responseWithUnauthUser.status.code)

        val user = userService.createUser("Rocky".toName(), "rocky@balboa.com".toEmail())
        val requestWithAuth =
            request
                .header("Authorization", user.getOrThrow().token.toString())
                .handlerWithAuth(userService::validateUser) { usr ->
                    Response(Status.OK).body(usr.toString())
                }
        assertEquals(requestWithAuth.status.code, 200)
        assertTrue(requestWithAuth.bodyString().contains(user.getOrThrow().toString()))
    }

    @Test
    fun testToAvailableHours() {
        val availableHour = listOf(0u, 1u, 3u, 4u, 5u, 6u, 7u, 8u)
        val expected =
            AvailableHours(
                listOf(
                    UIntInterval(0u, 2u),
                    UIntInterval(3u, 9u),
                ),
            )
        assertEquals(
            expected,
            availableHour.toAvailableHours(),
        )

        val otherHours =
            listOf(0u, 1u, 2u, 3u, 4u, 5u, 6u, 7u, 8u, 9u, 10u, 11u, 12u, 13u, 14u, 15u, 16u, 17u, 18u, 19u, 20u, 21u, 22u, 23u)
        val expectedOther =
            AvailableHours(
                listOf(
                    UIntInterval(0u, 24u),
                ),
            )
        assertEquals(
            expectedOther,
            otherHours.toAvailableHours(),
        )
    }
}
