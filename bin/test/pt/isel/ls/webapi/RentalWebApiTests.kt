@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.webapi

import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.repository.mem.TransactionManagerInMem
import pt.isel.ls.services.RentalService
import pt.isel.ls.services.UserService
import pt.isel.ls.webapi.dto.*
import kotlin.test.*

private val transactionManager = TransactionManagerInMem()

val rentalApi =
    RentalWebApi(
        RentalService(transactionManager),
        UserService(transactionManager),
    )
val rentalsRoutes =
    routes(
        "clubs/{cid}/courts/{crid}/rentals" bind POST to rentalApi::createRental,
        "clubs/{cid}/courts/{crid}/rentals" bind GET to rentalApi::getRentalsOnCourt,
        "clubs/{cid}/courts/{crid}/rentals/{rid}" bind GET to rentalApi::getRentalInfo,
        "users/{uid}/rentals" bind GET to rentalApi::getUserRentals,
        "clubs/{cid}/courts/{crid}/rentals/{rid}" bind DELETE to rentalApi::deleteRental,
        "clubs/{cid}/courts/{crid}/rentals/{rid}" bind PUT to rentalApi::updateRental,
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
            Request(POST, "clubs/${club.cid}/courts")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(Json.encodeToString(CourtCreationInput(name))),
        )
    assertEquals(Status.CREATED, courtResponse.status)
    val court = Json.decodeFromString<CourtDetailsOutput>(courtResponse.bodyString())

    val rental =
        rentalsRoutes(
            Request(POST, "clubs/${club.cid}/courts/${court.crid}/rentals")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(
                    Json
                        .encodeToString(
                            RentalCreationInput(
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
                Request(POST, "clubs/${club.cid}/courts/${court.crid}/rentals")
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .body(
                        Json.encodeToString<RentalCreationInput>(
                            RentalCreationInput(
                                date = LocalDate.parse("2025-06-15"),
                                initialHour = 10,
                                finalHour = 12,
                            ),
                        ),
                    ),
            )
        assertEquals(Status.CREATED, rental.status)
        val rentalDetails = Json.decodeFromString<RentalDetailsOutput>(rental.bodyString())
        assertEquals(rentalDetails.date, LocalDate.parse("2025-06-15"))
        assertEquals(rentalDetails.initialHour, 10)
        assertEquals(rentalDetails.finalHour, 12)
    }

    @Test
    fun `get rental info with valid rental id`() {
        val token = createUser()
        val rental = createRental(token)
        val getRentalInfoRequest =
            rentalsRoutes(
                Request(GET, "clubs/${rental.court.cid}/courts/${rental.court.crid}/rentals/1"),
            )
        assertEquals(Status.OK, getRentalInfoRequest.status)
    }

    @Test
    fun `get rental info with invalid rental id`() {
        val invalidRentalId = 999
        val getRentalInfoRequest =
            rentalsRoutes(
                Request(GET, "clubs/1/courts/1/rentals/$invalidRentalId"),
            )
        assertEquals(Status.NOT_FOUND, getRentalInfoRequest.status)
    }

    @Test
    fun `get all rentals for a court`() {
        val token = createUser()
        val rental = createRental(token)
        val getAllRentalsRequest =
            rentalsRoutes(
                Request(GET, "clubs/${rental.court.cid}/courts/${rental.court.crid}/rentals"),
            )
        val rentals = Json.decodeFromString<RentalsOutput>(getAllRentalsRequest.bodyString())
        assertTrue(rentals.rentals.any { it.date == rental.date })
        assertEquals(Status.OK, getAllRentalsRequest.status)
    }

    @Test
    fun `get user rentals with valid token`() {
        val name = randomString(10)
        val userResponse =
            userRoutes(
                Request(POST, "users")
                    .header("Content-Type", "application/json")
                    .body("""{"name":"Ric", "email":"$name@gmail.com"}"""),
            )
        assertEquals(Status.CREATED, userResponse.status)

        val getUserRentalsRequest =
            rentalsRoutes(
                Request(GET, "users/1/rentals"),
            )
        val userRentals = Json.decodeFromString<RentalsOutput>(getUserRentalsRequest.bodyString())
        assertTrue(userRentals.rentals.isEmpty())
        assertEquals(Status.OK, getUserRentalsRequest.status)
    }

    @Test
    fun `update the date and rentTime of a rental`() {
        val token = createUser()
        val rental = createRental(token)

        val updateRequest =
            rentalsRoutes(
                Request(PUT, "clubs/${rental.court.cid}/courts/${rental.court.crid}/rentals/1")
                    .header("Authorization", token)
                    .body(
                        Json.encodeToString<RentalUpdateInput>(
                            RentalUpdateInput(
                                date = LocalDate.parse("2026-06-15"),
                                initialHour = 16,
                                finalHour = 17,
                            ),
                        ),
                    ),
            )
        assertEquals(Status.OK, updateRequest.status)

        val updatedRental = Json.decodeFromString<RentalDetailsOutput>(updateRequest.bodyString())

        assertNotEquals(rental.date, updatedRental.date)
        assertNotEquals(rental.initialHour, updatedRental.initialHour)
        assertNotEquals(rental.finalHour, updatedRental.finalHour)
    }

    @Test
    fun `delete a rental`() {
        val token = createUser()
        val rental = createRental(token)
        assertNotNull(rental)

        val deleteRequest =
            rentalsRoutes(
                Request(DELETE, "clubs/${rental.court.cid}/courts/${rental.court.crid}/rentals/1")
                    .header("Authorization", token),
            )
        assertEquals(Status.OK, deleteRequest.status)

        val getRentalInfoRequest =
            rentalsRoutes(
                Request(GET, "clubs/${rental.court.cid}/courts/${rental.court.crid}/rentals/1")
                    .header("Authorization", token),
            )
        assertEquals(Status.NOT_FOUND, getRentalInfoRequest.status)
    }
}
