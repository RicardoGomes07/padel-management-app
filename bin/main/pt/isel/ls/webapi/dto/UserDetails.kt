package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.User

@Serializable
data class UserDetails(
    val uid: Int,
    val name: String,
    val email: String,
) {
    constructor(user: User) :
        this(user.uid.toInt(), user.name.value, user.email.value)
}
