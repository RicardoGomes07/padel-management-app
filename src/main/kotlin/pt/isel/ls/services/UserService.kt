package pt.isel.ls.services

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.Token
import pt.isel.ls.domain.User
import pt.isel.ls.repository.TransactionManager

class UserService(
    private val trxManager: TransactionManager,
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
            trxManager.run {
                userRepo.createUser(name, email)
            }
        }

    /**
     * Function that validates a user token
     * @param token the user token
     * @return the user if the token is valid, null otherwise
     */
    fun validateUser(token: Token): User? =
        trxManager.run {
            userRepo.findUserByToken(token)
        }

    /**
     * Function that returns a user by its identifier
     * @param uid the user identifier
     * @return the user if it exists, null otherwise
     */
    fun findUserById(uid: UInt): User? =
        trxManager.run {
            userRepo.findByIdentifier(uid)
        }
}
