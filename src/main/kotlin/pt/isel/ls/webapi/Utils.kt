package pt.isel.ls.webapi

import org.http4k.core.Request
import org.slf4j.LoggerFactory

/**
 * This is the Utils class, where you can see the information of a request.
 */

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
    fun verifyToken(request: Request): String? {
        val token = request.header("Authorization") ?: return null
        return token
    }
}
