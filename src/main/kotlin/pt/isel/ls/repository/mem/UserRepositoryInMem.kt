@file:OptIn(ExperimentalUuidApi::class, ExperimentalUuidApi::class)

package pt.isel.ls.repository.mem

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.User
import pt.isel.ls.repository.UserRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object UserRepositoryInMem : UserRepository {
    val users = mutableListOf<User>()

    private var currId: UInt = 0u

    override fun createUser(
        name: String,
        email: String,
    ) {
        val validEmail = Email(email)
        require(users.all { it.email != validEmail })
        currId += 1u
        val token = Uuid.random()
        val user =
            User(
                uid = currId,
                name = Name(name),
                email = validEmail,
                token = token,
            )

        users.add(user)
    }

    override fun findUserBYToken(token: Uuid): User? = users.firstOrNull{ it.token == token }

    override fun save(element: User) {
        val findUser = users.find { it.uid == element.uid }

        // user exists, so update
        if (findUser != null) {
            users.map { user ->
                if (user.uid == element.uid) {
                    element
                } else {
                    user
                }
            }
        } else {
            // add element to the user list
            users.add(element)
        }
    }

    override fun findByIdentifier(id: UInt): User? = users.firstOrNull { it.uid == id }

    override fun findAll(): List<User> = users

    override fun deleteByIdentifier(id: UInt) {
        users.removeIf { it.uid == id }
    }
}
