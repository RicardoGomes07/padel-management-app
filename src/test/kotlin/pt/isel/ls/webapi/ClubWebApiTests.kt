package pt.isel.ls.webapi

import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.Test
import kotlin.test.assertEquals

const val URI_API = "http://localhost:8080"

class ClubWebApiTests {
    private val client = OkHttp()
    private val user =
        Request(Method.POST, "$URI_API/users")
            .header("Content-Type", "application/json")
            .body("""{"name":"Riczão", "email":"riczao@gmail.com"}""")

    @Test
    fun `club creation with valid Name and User`() {
        val response = client(user)
        val token = response.bodyString().split("token")[1].split("\"")[2]
        val club =
            Request(Method.POST, "$URI_API/clubs")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body("""{"name":"Ric's Club"}""")
        val clubResponse = client(club)
        assertEquals(Status.OK, clubResponse.status)
        assertEquals("Expected Response Body", clubResponse.bodyString())
    }

    @Test
    fun `get club info with valid club id`() {
        val response = client(user)
        val token = response.bodyString().split("token")[1].split("\"")[2]
        val clubId = 1
        val getClubInfoRequest =
            Request(Method.GET, "$URI_API/clubs/$clubId")
                .header("Authorization", token)
        val getClubInfoResponse = client(getClubInfoRequest)
        assertEquals(Status.OK, getClubInfoResponse.status)
        assertEquals("Expected Response Body", getClubInfoResponse.bodyString())
    }

    @Test
    fun `get club info with invalid club id`() {
        val response = client(user)
        val token = response.bodyString().split("token")[1].split("\"")[2]
        val invalidClubId = 999
        val getClubInfoRequest =
            Request(Method.GET, "$URI_API/clubs/$invalidClubId")
                .header("Authorization", token)
        val getClubInfoResponse = client(getClubInfoRequest)
        assertEquals(Status.NOT_FOUND, getClubInfoResponse.status)
        assertEquals("Club not found", getClubInfoResponse.bodyString())
    }

    @Test
    fun `get all clubs with valid token`() {
        val response = client(user)
        val token = response.bodyString().split("token")[1].split("\"")[2]
        val getAllClubsRequest =
            Request(Method.GET, "$URI_API/clubs")
                .header("Authorization", token)
        val getAllClubsResponse = client(getAllClubsRequest)
        assertEquals(Status.OK, getAllClubsResponse.status)
    }

    @Test
    fun `get available hours with invalid date`() {
        val response = client(user)
        val token = response.bodyString().split("token")[1].split("\"")[2]
        val clubId = 1
        val courtId = 1
        val date = "2021-06-01"
        val getAvailableHoursRequest =
            Request(
                Method.GET,
                "$URI_API/clubs/$clubId/courts/$courtId/available?date=$date",
            ).header("Authorization", token).query("date", date)

        val getAvailableHoursResponse = client(getAvailableHoursRequest)
        assertEquals(Status.NOT_FOUND, getAvailableHoursResponse.status)
        assertEquals("No available hours found", getAvailableHoursResponse.bodyString())
    }
}
