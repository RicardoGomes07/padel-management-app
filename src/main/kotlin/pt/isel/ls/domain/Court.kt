package pt.isel.ls.domain

/**
 * Represents a court.
 * @property crid Unique identifier of the court.
 * @property name Name of the court.
 * @property club Club that owns the court.
 */
data class Court(
    val crid: UInt,
    val name: Name,
    val club: Club,
)
