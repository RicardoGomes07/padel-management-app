package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.domain.Name

@Serializable
data class OwnerOutput(
    val name: String,
    val uid: UInt,
) {
    constructor(uid: UInt, name: Name) :
        this(name.value, uid)
}
