@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.mem

import pt.isel.ls.domain.*
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.services.UserError
import pt.isel.ls.services.ensureOrThrow

object UserRepositoryInMem : UserRepository {
    val users = mutableListOf<User>()

    private var currId: UInt = 0u

    override fun createUser(
        name: Name,
        email: Email,
        password: Password,
    ): User {
        ensureOrThrow(
            condition = users.all { it.email != email },
            exception = UserError.UserAlreadyExists(email.value),
        )
        currId += 1u
        val user =
            User(
                uid = currId,
                name = name,
                email = email,
                password = password,
                token = null,
            )

        users.add(user)

        return users.first { it.uid == currId }
    }

    override fun login(
        email: Email,
        password: Password,
    ): User {
        val user = users.firstOrNull { it.email == email }
        requireNotNull(user)

        users.replaceAll { user ->
            if (user.email == email) {
                user.copy(token = generateToken())
            } else {
                user
            }
        }

        val loggedUser = users.firstOrNull { it.email == email }
        requireNotNull(loggedUser)
        return loggedUser
    }

    override fun logout(email: Email) {
        users.replaceAll { user ->
            if (user.email == email) {
                user.copy(token = null)
            } else {
                user
            }
        }
    }

    override fun findUserByToken(token: Token): User? = users.firstOrNull { it.token == token }

    override fun save(element: User) {
        users.removeIf { it.uid == element.uid }
        currId += 1u
        users.add(
            User(
                currId,
                element.name,
                element.email,
                element.password,
                element.token,
            ),
        )
    }

    override fun findByIdentifier(id: UInt): User? = users.firstOrNull { it.uid == id }

    override fun findAll(
        limit: Int,
        offset: Int,
    ): PaginationInfo<User> {
        val filteredUsers = users.drop(offset).take(limit)
        return PaginationInfo(filteredUsers, filteredUsers.size)
    }

    override fun deleteByIdentifier(id: UInt) {
        users.removeIf { it.uid == id }
    }

    override fun clear() {
        users.clear()
        currId = 0u
    }
}
