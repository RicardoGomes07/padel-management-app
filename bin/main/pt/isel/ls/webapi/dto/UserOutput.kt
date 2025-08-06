package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.User

@Serializable
data class UserOutput(
    val name: String,
    val token: String,
) {
    constructor(user: User) :
        this(user.name.value, user.token.toString())
}
