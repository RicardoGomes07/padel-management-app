@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.webapi

import kotlinx.datetime.*
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.slf4j.LoggerFactory
import pt.isel.ls.domain.Token
import pt.isel.ls.domain.User
import pt.isel.ls.domain.toToken

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

inline fun Request.handler(block: () -> Response): Response =
    try {
        log()
        block()
    } catch (e: Throwable) {
        e.toResponse()
    }

fun Request.handlerWithAuth(
    validationFun: (Token) -> User?,
    block: (User) -> Response,
): Response =
    try {
        log()
        validateUser(validationFun)
            ?.let { user -> block(user) }
            ?: Response(UNAUTHORIZED).body("Invalid User")
    } catch (e: Throwable) {
        e.toResponse()
    }

/**
 * Verifies if the user is valid and returns the user.
 */
private fun Request.validateUser(validateUser: (Token) -> User?): User? =
    header("Authorization")
        ?.toToken()
        ?.let { userToken -> validateUser(userToken) }

fun currentDate() =
    Clock
        .System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .toJavaLocalDateTime()
        .let { LocalDate(it.year, it.month, it.dayOfMonth) }

fun currentHour() =
    Clock
        .System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .hour

/**
 * Function that validates if a rental is in the future.
 * @param date Day of the rental to check
 * @param startHour Start hour of the rental to check
 * @return True if it's in the future, otherwise false
 */

fun isInTheFuture(
    date: LocalDate,
    startHour: Int,
): Boolean {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val currentHour =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .hour

    return when {
        date > today -> true
        date < today -> false
        else -> startHour > currentHour
    }
}
