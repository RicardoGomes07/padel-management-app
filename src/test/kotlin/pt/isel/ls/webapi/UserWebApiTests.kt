package pt.isel.ls.webapi

import junit.framework.TestCase.assertEquals
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.Test

class UserWebApiTests {
    private val client = OkHttp()

    @Test
    fun `user creation with valid Name and Email`() {
        val request = Request(Method.POST, "http://localhost:8080/users")
            .header("Content-Type", "application/json")
            .body("""{"name":"Riczão", "email":"riczao@gmail.com"}""")
        val response = client(request)

        assertEquals(Status.OK, response.status)
        assertEquals("Expected Response Body", response.bodyString())
    }

    @Test
    fun `user creation with duplicate Name and Email`() {
        val request = Request(Method.POST, "http://localhost:8080/users")
            .header("Content-Type", "application/json")
            .body("""{"name":"Ric", "email":"ric@gmail.com"}""")
        val response = client(request)
        assertEquals(Status.OK, response.status)

        val request1 = Request(Method.POST, "http://localhost:8080/users")
            .header("Content-Type", "application/json")
            .body("""{"name":"Ric", "email":"ric@gmail.com"}""")
        val response1 = client(request1)
        assertEquals(Status.BAD_REQUEST, response1.status)
    }

    @Test
    fun `get user info without auth`() {
        val request = Request(Method.GET, "http://localhost:8080/users/me")
        val response = client(request)

        assertEquals(Status.UNAUTHORIZED, response.status)
        assertEquals("Expected Response Body", response.bodyString())
    }

    @Test
    fun `get user info with auth`() {
        val request = Request(Method.POST, "http://localhost:8080/users")
            .header("Content-Type", "application/json")
            .body("""{"name":"Ric", "email":"ric@gmail.com"}""")
        val response = client(request)
        assertEquals(Status.OK, response.status)
        val token= response.bodyString().split("token")[1].split("\"")[2]

        val getUserInfoRequest = Request(Method.GET, "http://localhost:8080/users/me")
            .header("Authorization", token)
        val getUserInfoResponse = client(getUserInfoRequest)
        assertEquals(Status.OK, getUserInfoResponse.status)
    }
}
