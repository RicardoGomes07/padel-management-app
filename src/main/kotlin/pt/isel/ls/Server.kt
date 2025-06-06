package pt.isel.ls

import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.ls.repository.jdbc.TransactionManagerJdbc
import pt.isel.ls.services.ClubService
import pt.isel.ls.services.CourtService
import pt.isel.ls.services.RentalService
import pt.isel.ls.services.UserService
import pt.isel.ls.webapi.ClubWebApi
import pt.isel.ls.webapi.CourtWebApi
import pt.isel.ls.webapi.RentalWebApi
import pt.isel.ls.webapi.UserWebApi
import java.sql.DriverManager

private val logger = LoggerFactory.getLogger("HTTPServer")

val DB_URL = System.getenv("JDBC_DATABASE_URL") ?: throw Exception("Missing DB_URL environment variable")

fun main() {
    val connection = DriverManager.getConnection(DB_URL)
    val trxManagerJdbc = TransactionManagerJdbc(connection)

    val userApi = UserWebApi(UserService(trxManagerJdbc))
    val clubApi =
        ClubWebApi(
            ClubService(trxManagerJdbc),
            UserService(trxManagerJdbc),
        )
    val courtApi =
        CourtWebApi(
            CourtService(trxManagerJdbc),
            UserService(trxManagerJdbc),
        )
    val rentalApi =
        RentalWebApi(
            RentalService(trxManagerJdbc),
            UserService(trxManagerJdbc),
        )

    val authRoutes =
        routes(
            "/login" bind POST to userApi::login,
            "/logout" bind POST to userApi::logout,
        )

    val userRoutes =
        routes(
            "/" bind POST to userApi::createUser,
            "/{uid}" bind GET to userApi::getUserInfo,
            "/{uid}/rentals" bind GET to rentalApi::getUserRentals,
        )
    val clubsRoutes =
        routes(
            "/" bind POST to clubApi::createClub,
            "/" bind GET to clubApi::getAllClubs,
            "/{cid}" bind GET to clubApi::getClubInfo,
            "/{cid}/courts" bind POST to courtApi::createCourt,
            "/{cid}/courts" bind GET to courtApi::getCourtsByClub,
            "/{cid}/courts/available" bind POST to rentalApi::getAvailableCourtsByDateAndRentTime,
            "/{cid}/courts/{crid}" bind GET to courtApi::getCourtInfo,
            "/{cid}/courts/{crid}/available" bind POST to rentalApi::getAvailableHours,
            "/{cid}/courts/{crid}/rentals" bind POST to rentalApi::createRental,
            "/{cid}/courts/{crid}/rentals" bind GET to rentalApi::getRentalsOnCourt,
            "/{cid}/courts/{crid}/rentals/{rid}" bind GET to rentalApi::getRentalInfo,
            "/{cid}/courts/{crid}/rentals/{rid}" bind DELETE to rentalApi::deleteRental,
            "/{cid}/courts/{crid}/rentals/{rid}" bind PUT to rentalApi::updateRental,
        )

    val app =
        routes(
            "/auth" bind authRoutes,
            "/users" bind userRoutes,
            "/clubs" bind clubsRoutes,
            singlePageApp(ResourceLoader.Directory("static-content")),
        )

    val jettyServer = app.asServer(Jetty(9000)).start()
    logger.info("server started listening")

    readln()
    jettyServer.stop()

    connection.close()

    logger.info("leaving Main")
}
