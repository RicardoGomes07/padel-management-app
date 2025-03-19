package pt.isel.ls.webapi

import org.http4k.core.Request
import org.http4k.core.Response

/**
 * This is the Rental Management Api, where you can see details about a rental or create one.
 */

class RentalWebApi {
    // private val rentalService = TODO()

    fun createRental(request: Request): Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getAllRentals(request: Request): Response {
        Utils.logRequest(request)
        return TODO()
    }

    fun getRentalInfo(request: Request): Response {
        Utils.logRequest(request)
        return TODO()
    }
}
