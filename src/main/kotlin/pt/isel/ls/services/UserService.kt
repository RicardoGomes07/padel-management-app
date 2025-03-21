package pt.isel.ls.services

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.Token
import pt.isel.ls.domain.User
import pt.isel.ls.repository.UserRepository

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
    ): Result<User> =
        runCatching {
            userRepo.createUser(name, email)
        }

    /**
     * Function that validates a user token
     * @param token the user token
     * @return the user if the token is valid, null otherwise
     */
    fun validateUser(token: Token): User? = userRepo.findUserByToken(token)
}
