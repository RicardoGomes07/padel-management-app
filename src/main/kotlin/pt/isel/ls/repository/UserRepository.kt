@file:OptIn(ExperimentalUuidApi::class)

package pt.isel.ls.repository

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Generic Interface for a User repository that supports CRUD operations.
 */
interface UserRepository : Repository<User> {
    /**
     * Function that creates a User.
     * uid is automatically incremented so it's not received as a parameter to the function.
     */
    fun createUser(
        name: Name,
        email: Email,
    ): User

    fun findUserBYToken(token: Uuid): User?
}
