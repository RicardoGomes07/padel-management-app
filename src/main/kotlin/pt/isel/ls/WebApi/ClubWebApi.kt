package pt.isel.ls.WebApi

import org.http4k.core.Request
import org.http4k.core.Response

class ClubWebApi {
    //private val clubService = TODO() // service

    fun createClub (request: Request) : Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getAllClubs (request: Request) : Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getClubInfo (request: Request) : Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getAvailableHours (request: Request) : Response {
        Utils.logRequest(request)
        return TODO()
    }
}