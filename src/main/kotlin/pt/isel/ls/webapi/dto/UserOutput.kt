package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class UserOutput(
    val name: String,
    val token: Uuid,
) {
    constructor(user: User) :
        this(user.name.value, user.token)
}
