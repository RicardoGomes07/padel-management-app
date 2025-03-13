package pt.isel.ls.domain

/**
 * Represents a user.
 * @property uid Unique identifier of the user.
 * @property name Name of the user.
 * @property email Unique email of the user.
 */
data class User(
    val uid: UInt,
    val name: Name,
    val email: Email,
)
