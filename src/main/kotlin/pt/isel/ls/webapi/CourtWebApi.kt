package pt.isel.ls.WebApi

import org.http4k.core.Request
import org.http4k.core.Response

/**
 * This is the Court Management Api, where you can see details about a court or create one.
 */

class CourtWebApi {
    //private val courtService = TODO()

    fun createCourt(request: Request) : Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getCourtsByClub(request: Request) : Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getCourtInfo(request: Request) : Response {
        Utils.logRequest(request)
        return TODO()
    }
}