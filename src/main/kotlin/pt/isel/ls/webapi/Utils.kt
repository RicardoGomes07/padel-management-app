package pt.isel.ls.webapi

import org.http4k.core.Request
import org.slf4j.LoggerFactory
import pt.isel.ls.domain.Token
import pt.isel.ls.domain.User
import java.sql.SQLException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * This is the Utils class, where you can see the information of a request.
 */
@OptIn(ExperimentalUuidApi::class)
object Utils {
    private val logger = LoggerFactory.getLogger("HTTPServer")

    fun logRequest(request: Request) {
        logger.info(
            "incoming request: method={}, uri={}, content-type={} accept={}",
            request.method,
            request.uri,
            request.header("content-type"),
            request.header("accept"),
        )
    }

    fun verifyAndValidateUser(
        request: Request,
        validateUser: (Token) -> User?,
    ): User? =
        request
            .header("Authorization")
            ?.let { token -> Token(Uuid.parse(token)) }
            ?.let { userToken -> validateUser(userToken) }

    fun runWithExceptionHandling(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            when {
                e is SQLException -> {
                    logger.error(
                        "SQL Exception: state={}, code={}, message={}",
                        e.sqlState,
                        e.errorCode,
                        e.message,
                    )
                }
                else -> throw e
            }
        }
    }
}
