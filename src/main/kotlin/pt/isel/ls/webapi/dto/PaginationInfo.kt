package pt.isel.ls.webapi.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.webapi.LIMIT_VALUE_DEFAULT
import pt.isel.ls.webapi.SKIP_VALUE_DEFAULT

@Serializable
data class PaginationInfo(
    val totalElements: Int,
    val defaultLimit: Int = LIMIT_VALUE_DEFAULT,
    val defaultSkip: Int = SKIP_VALUE_DEFAULT,
)
