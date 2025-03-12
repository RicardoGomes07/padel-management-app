package pt.isel.ls.domain

/**
 * Represents a Club.
 * @property cid Unique identifier of the club.
 * @property name Unique name of the club.
 * @property owner User that owns the club.
 */
data class Club(
    val cid: Int,
    val name: String,
    val owner: User,
)
