package pt.isel.ls.repository.jdbc.dao

import pt.isel.ls.domain.Name
import java.sql.ResultSet

data class ClubDb(
    val cid: UInt,
    val name: Name,
    val owner: UInt,
)

fun ResultSet.mapClubDb() =
    ClubDb(
        cid = getInt("club_id").toUInt(),
        name = Name(getString("club_name")),
        owner = getInt("owner_id").toUInt(),
    )
