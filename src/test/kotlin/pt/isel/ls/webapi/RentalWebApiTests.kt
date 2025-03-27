package pt.isel.ls.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.repository.mem.TransactionManagerInMem
import pt.isel.ls.services.RentalService
import pt.isel.ls.services.UserService
import pt.isel.ls.webapi.dto.RentalDetailsOutput
import pt.isel.ls.webapi.dto.RentalsOutput
import kotlin.test.Test
import kotlin.test.assertEquals

val rentalApi =
    RentalWebApi(
        RentalService(TransactionManagerInMem()),
        UserService(TransactionManagerInMem()),
    )
val rentalsRoutes =
    routes(
        "rentals" bind POST to rentalApi::createRental,
        "rentals/clubs/courts/{crid}" bind GET to rentalApi::getAllRentals,
        "rentals/{rid}" bind GET to rentalApi::getRentalInfo,
        "users/rentals" bind GET to rentalApi::getUserRentals,
    )

fun createRental(
    token: String,
    cid: Int,
    crid: Int,
): RentalDetailsOutput {
    val number = (1..30).random()
    val day = if (number < 10) "0$number" else number.toString()
    val rental =
        rentalsRoutes(
            Request(POST, "rentals")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body("""{"date":"2025-06-$day","initialHour":10,"finalHour":12,"cid":$cid,"crid":$crid}"""),
        )
    return Json.decodeFromString<RentalDetailsOutput>(rental.bodyString())
}

class RentalWebApiTests {
    @Test
    fun `create rental with valid data`() {
        val token = createUser()
        val club = createClub(token)
        val court = createCourt(token, club.cid.toInt())
        val rental =
            rentalsRoutes(
                Request(POST, "rentals")
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .body(
                        """{"date":"2025-06-15","initialHour":10,"finalHour":12,"cid":${club.cid.toInt()},"crid":${court.crid.toInt()}}""",
                    ),
            )
        assertEquals(Status.CREATED, rental.status)
    }

    @Test
    fun `get rental info with valid rental id`() {
        val token = createUser()
        createRental(token, 1, 1)
        val getRentalInfoRequest =
            rentalsRoutes(
                Request(GET, "rentals/1")
                    .header("Authorization", token),
            )
        val rental = Json.decodeFromString<RentalDetailsOutput>(getRentalInfoRequest.bodyString())
        println(rental)
        assertEquals(Status.OK, getRentalInfoRequest.status)
    }

    @Test
    fun `get rental info with invalid rental id`() {
        val token = createUser()
        val invalidRentalId = 999
        val getRentalInfoRequest =
            rentalsRoutes(
                Request(GET, "rentals/$invalidRentalId")
                    .header("Authorization", token),
            )
        assertEquals(Status.NOT_FOUND, getRentalInfoRequest.status)
    }

    @Test
    fun `get all rentals for a court`() {
        val token = createUser()
        createRental(token, 1, 1)
        val getAllRentalsRequest =
            rentalsRoutes(
                Request(GET, "rentals/clubs/courts/1")
                    .header("Authorization", token),
            )
        val rentals = Json.decodeFromString<RentalsOutput>(getAllRentalsRequest.bodyString())
        println(rentals)
        assertEquals(Status.OK, getAllRentalsRequest.status)
    }

    @Test
    fun `get user rentals with valid token`() {
        val token = createUser()
        val getUserRentalsRequest =
            rentalsRoutes(
                Request(GET, "users/rentals")
                    .header("Authorization", token),
            )
        val userRentals = Json.decodeFromString<RentalsOutput>(getUserRentalsRequest.bodyString())
        println(userRentals)
        assertEquals(Status.OK, getUserRentalsRequest.status)
    }
}
