package pt.isel.ls

import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.ls.webapi.ClubWebApi
import pt.isel.ls.webapi.CourtWebApi
import pt.isel.ls.webapi.RentalWebApi
import pt.isel.ls.webapi.UserWebApi

private val logger = LoggerFactory.getLogger("HTTPServer")

fun main() {
    val userApi = UserWebApi()
    val clubApi = ClubWebApi()
    val courtApi = CourtWebApi()
    val rentalApi = RentalWebApi()

    val userRoutes =
        routes(
        "/" bind POST to userApi::createUser,
        "/me" bind GET to userApi::getUserInfo,
        "/rentals" bind GET to userApi::getUserRentals,
        )
    val clubsRoutes =
        routes(
        "/" bind POST to clubApi::createClub,
        "/" bind GET to clubApi::getAllClubs,
        "/{cid}" bind GET to clubApi::getClubInfo,
        "/{cid}/courts/{crid}/available" bind GET to clubApi::getAvailableHours,
        )
    val courtsRoutes =
        routes(
        "/" bind POST to courtApi::createCourt,
        "/{cid}" bind GET to courtApi::getCourtsByClub,
        "/{crid}" bind GET to courtApi::getCourtInfo,
        )
    val rentalsRoutes =
        routes(
        "/" bind POST to rentalApi::createRental,
        "/clubs/{cid}/courts/{crid}" bind GET to rentalApi::getAllRentals,
        "/{rid}" bind GET to rentalApi::getRentalInfo,
        )

    val app = routes(
        "/users" bind userRoutes,
        "/clubs" bind clubsRoutes,
        "/courts" bind courtsRoutes,
        "/rentals" bind rentalsRoutes,
    )

    val jettyServer = app.asServer(Jetty(8080)).start()
    logger.info("server started listening")

    readln()
    jettyServer.stop()

    logger.info("leaving Main")
}