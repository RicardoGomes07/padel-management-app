package pt.isel.ls

import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.ls.repository.jdbc.TransactionManagerJdbc
import pt.isel.ls.repository.mem.TransactionManagerInMem
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
    //val connection = DriverManager.getConnection(DB_URL)
    //val trxManagerJdbc = TransactionManagerJdbc(connection)
    val trxManagerInMem = TransactionManagerInMem()

    val userApi = UserWebApi(UserService(trxManagerInMem))
    val clubApi =
        ClubWebApi(
            ClubService(trxManagerInMem),
            UserService(trxManagerInMem),
            RentalService(trxManagerInMem),
        )
    val courtApi =
        CourtWebApi(
            CourtService(trxManagerInMem),
            UserService(trxManagerInMem),
        )
    val rentalApi =
        RentalWebApi(
            RentalService(trxManagerInMem),
            UserService(trxManagerInMem),
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
            "/{cid}/courts/{crid}/available" bind POST to clubApi::getAvailableHours,
        )
    val courtsRoutes =
        routes(
            "/" bind POST to courtApi::createCourt,
            "/clubs/{cid}" bind GET to courtApi::getCourtsByClub,
            "/{crid}" bind GET to courtApi::getCourtInfo,
        )
    val rentalsRoutes =
        routes(
            "/" bind POST to rentalApi::createRental,
            "/clubs/{cid}/courts/{crid}" bind GET to rentalApi::getAllRentals,
            "/{rid}" bind GET to rentalApi::getRentalInfo,
            "/{rid}" bind DELETE to rentalApi::deleteRental,
            "/{rid}" bind PUT to rentalApi::updateRental,
        )

    val app =
        routes(
            "/users" bind userRoutes,
            "/clubs" bind clubsRoutes,
            "/courts" bind courtsRoutes,
            "/rentals" bind rentalsRoutes,
        )

    val jettyServer = app.asServer(Jetty(8080)).start()
    logger.info("server started listening")

    readln()
    jettyServer.stop()

    //connection.close()

    logger.info("leaving Main")
}
