package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.User

@Serializable
data class AuthUser(
    val id: UInt,
    val token: String,
) {
    constructor(user: User) : this(user.uid, user.token.toString())
}
