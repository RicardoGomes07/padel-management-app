package pt.isel.ls.services

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.User
import pt.isel.ls.repository.UserRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed class UserErrors {
    data object UserEmailAlreadyExists : UserErrors()
}

class UserService(
    private val userRepo: UserRepository,
) {
    /**
     * Function that creates a new user in the system
     * @param name the username
     * @param email the user email
     */
    fun createUser(
        name: Name,
        email: Email,
    ): Either<UserErrors.UserEmailAlreadyExists, User> =
        try {
            val user = userRepo.createUser(name, email)
            success(user)
        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> failure(UserErrors.UserEmailAlreadyExists)
                else -> throw ex // Other Data Base exceptions
            }
        }

    /**
     * Function that validates a user token
     * @param token the user token
     * @return the user if the token is valid, null otherwise
     */
    @OptIn(ExperimentalUuidApi::class)
    fun validateUser(token: Uuid): User? = userRepo.findUserBYToken(token)
}
