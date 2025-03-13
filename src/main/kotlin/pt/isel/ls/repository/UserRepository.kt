package pt.isel.ls.repository

import pt.isel.ls.domain.User

/**
 * Generic Interface for a User repository that supports CRUD operations.
 */
interface UserRepository : Repository<User> {
    /**
     * Function that creates a User.
     * Returns the created element.
     * uid is automatically incremented so it's not received as a parameter to the function.
     */
    fun createUser(
        name: String,
        email: String,
    ): User
}
