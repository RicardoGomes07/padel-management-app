@file:OptIn(ExperimentalUuidApi::class)

package pt.isel.ls.repository.mem

import pt.isel.ls.domain.Email
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.Token
import pt.isel.ls.domain.User
import pt.isel.ls.repository.UserRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object UserRepositoryInMem : UserRepository {
    val users = mutableListOf<User>()

    private var currId: UInt = 0u

    override fun createUser(
        name: Name,
        email: Email,
    ): User {
        require(users.all { it.email != email })
        currId += 1u
        val value = Uuid.random()
        val user =
            User(
                uid = currId,
                name = name,
                email = email,
                token = Token(value),
            )

        users.add(user)
        return user
    }

    override fun findUserByToken(token: Token): User? = users.firstOrNull { it.token.value == token.value }

    override fun save(element: User) {
        users.removeIf { it.uid == element.uid }
        users.add(element)
    }

    override fun findByIdentifier(id: UInt): User? = users.firstOrNull { it.uid == id }

    override fun findAll(): List<User> = users

    override fun deleteByIdentifier(id: UInt) {
        users.removeIf { it.uid == id }
    }

    override fun clear() {
        users.clear()
    }
}
