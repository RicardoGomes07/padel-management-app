package pt.isel.ls

import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes

/**
 * Main function that instances the routes for the application.

fun main() {
    val userRoutes =
        routes(
            "/" bind POST to ::createUser,
            "/me" bind GET to ::getUserInfo,
            "/rentals" bind GET to ::getUserRentals,
        )
    val clubsRoutes =
        routes(
            "/" bind POST to ::createClub,
            "/" bind GET to ::getAllClubs,
            "/{cid}" bind GET to ::getClubInfo,
            "/{cid}/courts/{crid}/available" bind GET to ::getAvailableHours,
        )
    val courtsRoutes =
        routes(
            "/" bind POST to ::createCourt,
            "/{cid}" bind GET to ::getCourtsByClub,
            "/{crid}" bind GET to ::getCourtInfo,
        )
    val rentalsRoutes =
        routes(
            "/" bind POST to ::createRental,
            "/clubs/{cid}/courts/{crid}" bind GET to ::getAllRentals,
            "/{rid}" bind GET to ::getRentalInfo,
        )
}*/