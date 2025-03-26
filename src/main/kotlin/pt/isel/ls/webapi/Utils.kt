@file:OptIn(ExperimentalUuidApi::class, ExperimentalUuidApi::class)

package pt.isel.ls.webapi

import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNPROCESSABLE_ENTITY
import org.slf4j.LoggerFactory
import pt.isel.ls.domain.Token
import pt.isel.ls.domain.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

const val LIMIT_VALUE_DEFAULT = 10
const val SKIP_VALUE_DEFAULT = 0

/**
 * This is the Utils class, where you can see the information of a request.
 */

private val logger = LoggerFactory.getLogger("HTTPServer")

fun Request.log() {
    logger.info(
        "incoming request: method={}, uri={}, content-type={} accept={}",
        method,
        uri,
        header("content-type"),
        header("accept"),
    )
}

fun <T> validateUserInput(operation: () -> T): Result<T> =
    runCatching {
        operation()
    }

fun handleUserInputError(e: Throwable): Response =
    when (e) {
        is IllegalArgumentException -> Response(BAD_REQUEST).body(e.message!!)
        else -> throw e
    }

/**
 * Verifies if the user is valid and returns the user.
 */
fun Request.validateUser(validateUser: (Token) -> User?): User? =
    header("Authorization")
        ?.let { token -> Token(Uuid.parse(token)) }
        ?.let { userToken -> validateUser(userToken) }

fun Throwable.toResponse(): Response =
    when (this) {
        is IllegalStateException -> Response(NOT_FOUND).body(message!!)
        is IllegalArgumentException -> Response(BAD_REQUEST).body(message!!)
        else -> Response(UNPROCESSABLE_ENTITY).body(message!!)
    }
