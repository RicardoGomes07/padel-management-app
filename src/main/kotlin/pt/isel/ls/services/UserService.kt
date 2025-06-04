package pt.isel.ls.services

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.Password
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
        password: Password,
    ): Result<User> =
        runCatching {
            trxManager.run {
                userRepo.createUser(name, email, password)
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
     * Function that logs a user in the system
     * @param email the user email
     * @param password the user password
     * @return the user if the login is successful, an error otherwise
     */
    fun login(
        email: Email,
        password: Password,
    ): Result<User> =
        runCatching {
            trxManager.run {
                userRepo.login(email, password)
            }
        }

    /**
     * Function that logs a user out of the system
     * @param user the user to log out
     * @return a result indicating the success or failure of the operation
     */
    fun logout(user: User): Result<Unit> =
        runCatching {
            trxManager.run {
                userRepo.logout(user.email)
            }
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
