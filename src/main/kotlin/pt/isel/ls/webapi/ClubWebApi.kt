package pt.isel.ls.webapi

import org.http4k.core.Request
import org.http4k.core.Response

/**
 * This is the Club Management Api, where you can see details about a club or create one.
 */

class ClubWebApi {
    // private val clubService = TODO() // service

    fun createClub(request: Request): Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getAllClubs(request: Request): Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getClubInfo(request: Request): Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getAvailableHours(request: Request): Response {
        Utils.logRequest(request)
        return TODO()
    }
}
