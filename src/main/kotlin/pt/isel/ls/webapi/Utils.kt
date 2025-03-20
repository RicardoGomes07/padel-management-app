package pt.isel.ls.webapi

import org.http4k.core.Request
import org.slf4j.LoggerFactory
import pt.isel.ls.domain.User
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
    fun verifyAndValidateUser(request: Request, validateUser: (Uuid) -> User?): User? {
        return request.header("Authorization")
            ?.let { token -> Uuid.parse(token) }
            ?.let { userToken -> validateUser(userToken) }
    }
}
