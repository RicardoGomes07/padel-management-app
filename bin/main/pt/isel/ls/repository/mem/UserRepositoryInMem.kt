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
    ): User {
        ensureOrThrow(
            condition = users.all { it.email != email },
            exception = UserError.UserAlreadyExists(email.value),
        )
        currId += 1u
        val token = generateToken()
        val user =
            User(
                uid = currId,
                name = name,
                email = email,
                token = token,
            )

        users.add(user)
        return user
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
                element.token,
            ),
        )
    }

    override fun findByIdentifier(id: UInt): User? = users.firstOrNull { it.uid == id }

    override fun findAll(
        limit: Int,
        offset: Int,
    ): List<User> = users.drop(offset).take(limit)

    override fun deleteByIdentifier(id: UInt) {
        users.removeIf { it.uid == id }
    }

    override fun clear() {
        users.clear()
        currId = 0u
    }
}
