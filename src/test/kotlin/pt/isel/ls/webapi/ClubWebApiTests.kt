package pt.isel.ls.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.repository.mem.TransactionManagerInMem
import pt.isel.ls.services.ClubService
import pt.isel.ls.services.RentalService
import pt.isel.ls.services.UserService
import pt.isel.ls.webapi.dto.ClubDetailsOutput
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

val clubApi =
    ClubWebApi(
        ClubService(TransactionManagerInMem()),
        UserService(TransactionManagerInMem()),
        RentalService(TransactionManagerInMem()),
    )

val clubsRoutes =
    routes(
        "clubs" bind POST to clubApi::createClub,
        "clubs" bind GET to clubApi::getAllClubs,
        "clubs/{cid}" bind GET to clubApi::getClubInfo,
        "clubs/{cid}/courts/{crid}/available" bind GET to clubApi::getAvailableHours,
    )

fun createClub(token: String): ClubDetailsOutput {
    val name = "Club-${UUID.randomUUID().toString().take(6)}"
    val clubResponse =
        clubsRoutes(
            Request(POST, "clubs")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body("""{"name":"$name"}"""),
        )
    return Json.decodeFromString<ClubDetailsOutput>(clubResponse.bodyString())
}

class ClubWebApiTests {
    @Test
    fun `club creation with valid Name and User`() {
        val token = createUser()
        val clubResponse =
            clubsRoutes(
                Request(POST, "clubs")
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .body("""{"name":"Benfica"}"""),
            )
        assertEquals(Status.CREATED, clubResponse.status)
        val club = Json.decodeFromString<ClubDetailsOutput>(clubResponse.bodyString())
        println(club)
        assertEquals(club.name, "Benfica")
    }

    @Test
    fun `get club info with valid club id`() {
        val token = createUser()
        println(token)
        val club = createClub(token)
        val clubResponse =
            clubsRoutes(
                Request(GET, "clubs/${club.cid}")
                    .header("Authorization", token),
            )
        val clubRequest = Json.decodeFromString<ClubDetailsOutput>(clubResponse.bodyString())
        println(clubRequest)
        assertEquals(club.name, clubRequest.name)
        assertEquals(Status.OK, clubResponse.status)
    }

    @Test
    fun `get club info with invalid club id`() {
        val invalidClubId = 999
        val token = createUser()
        val getClubInfoRequest =
            clubsRoutes(
                Request(GET, "clubs/$invalidClubId")
                    .header("Authorization", token),
            )
        assertEquals(Status.NOT_FOUND, getClubInfoRequest.status)
    }

    @Test
    fun `get all clubs with valid token`() {
        val token = createUser()
        val getAllClubsRequest =
            clubsRoutes(
                Request(GET, "clubs")
                    .header("Authorization", token),
            )
        assertEquals(Status.OK, getAllClubsRequest.status)
        println(getAllClubsRequest.bodyString())
    }

    @Test
    fun `get available hours with invalid court`() {
        val clubId = 1
        val courtId = 999
        val date = "2025-06-01"
        val token = createUser()
        val getAvailableHoursRequest =
            clubsRoutes(
                Request(GET, "clubs/$clubId/courts/$courtId/available")
                    .header("Authorization", token)
                    .query("date", date),
            )
        assertEquals(Status.NOT_FOUND, getAvailableHoursRequest.status)
    }

    @Test
    fun `get available hours`() {
        val token = createUser()
        val clubId = createClub(token).cid
        val courtId = createCourt(token, clubId.toInt())
        val date = "2025-06-01"

        val getAvailableHoursRequest =
            clubsRoutes(
                Request(GET, "clubs/$clubId/courts/${courtId.crid}/available")
                    .header("Authorization", token)
                    .query("date", date),
            )
        assertEquals(Status.NOT_FOUND, getAvailableHoursRequest.status)
    }
}
