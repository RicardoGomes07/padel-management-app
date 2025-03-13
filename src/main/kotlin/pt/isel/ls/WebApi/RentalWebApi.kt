package pt.isel.ls.WebApi

import org.http4k.core.Request
import org.http4k.core.Response

class RentalWebApi {
    //private val rentalService = TODO()

    fun createRental(request: Request) : Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getAllRentals(request: Request) : Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getRentalInfo(request: Request) : Response {
        Utils.logRequest(request)
        return TODO()
    }
}