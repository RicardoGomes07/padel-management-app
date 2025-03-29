package pt.isel.ls.webapi

import kotlinx.datetime.LocalDate
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
import pt.isel.ls.webapi.dto.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val transactionManager = TransactionManagerInMem()

val rentalApi =
    RentalWebApi(
        RentalService(transactionManager),
        UserService(transactionManager),
    )
val rentalsRoutes =
    routes(
        "rentals" bind POST to rentalApi::createRental,
        "rentals/clubs/courts/{crid}" bind GET to rentalApi::getAllRentals,
        "rentals/{rid}" bind GET to rentalApi::getRentalInfo,
        "users/rentals" bind GET to rentalApi::getUserRentals,
    )

fun createRental(token: String): RentalDetailsOutput {
    val number = (1..30).random()
    val day = if (number < 10) "0$number" else number.toString()
    val clubName = "Club-${randomString(10)}"
    val clubResponse =
        clubsRoutes(
            Request(POST, "clubs")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(Json.encodeToString<ClubCreationInput>(ClubCreationInput(clubName))),
        )
    assertEquals(Status.CREATED, clubResponse.status)
    val club = Json.decodeFromString<ClubDetailsOutput>(clubResponse.bodyString())
    val name = "Court-${randomString(10)}"
    val courtResponse =
        courtsRoutes(
            Request(POST, "courts")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body("""{"cid":${club.cid.toInt()},"name":"$name"}"""),
        )
    assertEquals(Status.CREATED, courtResponse.status)
    val court = Json.decodeFromString<CourtDetailsOutput>(courtResponse.bodyString())

    val rental =
        rentalsRoutes(
            Request(POST, "rentals")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(
                    Json
                        .encodeToString(
                            RentalCreationInput(
                                club.cid.toInt(),
                                court.crid.toInt(),
                                LocalDate.parse("2025-06-$day"),
                                10,
                                12,
                            ),
                        ),
                ),
        )
    assertEquals(Status.CREATED, rental.status)
    return Json.decodeFromString<RentalDetailsOutput>(rental.bodyString())
}

class RentalWebApiTests {
    @BeforeTest
    fun setup() {
        transactionManager.run {
            it.rentalRepo.clear()
            it.courtRepo.clear()
            it.clubRepo.clear()
            it.userRepo.clear()
        }
    }

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
                        Json.encodeToString<RentalCreationInput>(
                            RentalCreationInput(
                                date = LocalDate.parse("2025-06-15"),
                                initialHour = 10,
                                finalHour = 12,
                                cid = club.cid.toInt(),
                                crid = court.crid.toInt(),
                            ),
                        ),
                    ),
            )
        assertEquals(Status.CREATED, rental.status)
        val rentalDetails = Json.decodeFromString<RentalDetailsOutput>(rental.bodyString())
        assertEquals(rentalDetails.date, LocalDate.parse("2025-06-15"))
        assertEquals(rentalDetails.initialHour, 10)
        assertEquals(rentalDetails.finalHour, 12)
        assertEquals(rentalDetails.court.club.cid, club.cid)
    }

    @Test
    fun `get rental info with valid rental id`() {
        val token = createUser()
        createRental(token)
        val getRentalInfoRequest =
            rentalsRoutes(
                Request(GET, "rentals/1")
                    .header("Authorization", token),
            )
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
        val rental = createRental(token)
        val getAllRentalsRequest =
            rentalsRoutes(
                Request(GET, "rentals/clubs/courts/1")
                    .header("Authorization", token),
            )
        val rentals = Json.decodeFromString<RentalsOutput>(getAllRentalsRequest.bodyString())
        assertTrue(rentals.rentals.contains(rental))
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
        assertTrue(userRentals.rentals.isEmpty())
        assertEquals(Status.OK, getUserRentalsRequest.status)
    }
}
