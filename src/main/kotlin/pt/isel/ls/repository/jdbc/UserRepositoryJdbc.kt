@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.domain.*
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.services.UserError
import pt.isel.ls.services.ensureOrThrow
import java.sql.Connection
import java.sql.ResultSet

/**
 * Repository in jdbc responsible for direct interactions with the database for users related actions
 * @param connection The database connection for the SQL queries
 */
class UserRepositoryJdbc(
    private val connection: Connection,
) : UserRepository {
    /**
     * Function responsible for the creation of a user.
     * @param name Name of the new User
     * @param email Email of the new User
     * @return The User created
     * @throws IllegalArgumentException if the email is not unique
     */
    override fun createUser(
        name: Name,
        email: Email,
    ): User {
        val token = generateToken()

        val sqlInsert =
            """
            INSERT INTO users (name, email, token)
            VALUES (?, ?, ?)
            ON CONFLICT (email) DO NOTHING
            RETURNING *;
            """.trimIndent()

        return connection.prepareStatement(sqlInsert).use { stmt ->
            stmt.setString(1, name.value)
            stmt.setString(2, email.value)
            stmt.setString(3, token.toString())

            stmt.executeQuery().use { rs ->
                ensureOrThrow(
                    condition = rs.next(),
                    exception = UserError.UserAlreadyExists(email.value),
                )
                rs.mapUser()
            }
        }
    }

    /**
     * Function that finds a User by a given token.
     * @param token The token to search respective user
     * @return The respective User if found, otherwise null
     */
    override fun findUserByToken(token: Token): User? {
        val sqlSelect = "SELECT * FROM users WHERE token = ?"

        return connection.prepareStatement(sqlSelect).use { stmt ->
            stmt.setString(1, token.toString())
            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.mapUser() else null
            }
        }
    }

    /**
     * Function that creates a new User or updates, with the information given, if one with the uid already exists.
     * @param element User to be created or updated
     */
    override fun save(element: User) {
        val sqlSave =
            """
            INSERT INTO users (name, email, token)
            VALUES (?, ?, ?)
            ON CONFLICT (email, token)
            DO UPDATE SET
                name = EXCLUDED.name,
                email = EXCLUDED.email,
                token = EXCLUDED.token;
            """.trimIndent()

        connection.prepareStatement(sqlSave).use { stmt ->
            stmt.setString(1, element.name.value)
            stmt.setString(2, element.email.value)
            stmt.setString(3, element.token.toString())

            stmt.executeUpdate()
        }
    }

    /**
     * Function that finds a User by its uid.
     * @param id Identifier of the class, corresponding to the PK of the respective table, in this case uid
     * @return User if found, otherwise null
     */
    override fun findByIdentifier(id: UInt): User? {
        val sqlSelect = "SELECT * FROM users WHERE uid = ?"

        return connection.prepareStatement(sqlSelect).use { stmt ->
            stmt.setInt(1, id.toInt())

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.mapUser() else null
            }
        }
    }

    /**
     * Function that returns limit elements after offset, from latest tuple to be created to oldest
     * @param limit Number of tuples to retrieve, default of 30
     * @param offset Number of tuples to skip at the beginning, default of 0
     * @return List of Users retrieved
     */
    override fun findAll(
        limit: Int,
        offset: Int,
    ): PaginationInfo<User> =
        connection.executeMultipleQueries {
            val sqlSelect =
                """
                SELECT * FROM users
                ORDER BY uid DESC
                LIMIT ? OFFSET ?
                """.trimIndent()

            val users =
                connection.prepareStatement(sqlSelect).use { stmt ->
                    stmt.setInt(1, limit)
                    stmt.setInt(2, offset)

                    stmt.executeQuery().use { rs ->
                        val users = mutableListOf<User>()
                        while (rs.next()) {
                            users.add(rs.mapUser())
                        }
                        users
                    }
                }

            val sqlCount = "SELECT COUNT(*) FROM users"
            val count =
                connection.prepareStatement(sqlCount).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) rs.getInt(1) else 0
                    }
                }

            return@executeMultipleQueries PaginationInfo(users, count)
        }

    /**
     * Function that deletes a User if exists a tuple with the uid, if it doesn't exist, does nothing
     * @param id Identifier of the User to delete
     */
    override fun deleteByIdentifier(id: UInt) {
        val sqlDelete = "DELETE FROM users WHERE uid = ?"

        connection.prepareStatement(sqlDelete).use { stmt ->
            stmt.setInt(1, id.toInt())
            stmt.executeUpdate()
        }
    }

    /**
     * Function that deletes every entry of the table,
     *  resets autoincremented values and any rows that have references to it
     */
    override fun clear() {
        val sqlDelete = "TRUNCATE TABLE users RESTART IDENTITY CASCADE"
        connection.prepareStatement(sqlDelete).use { stmt ->
            stmt.executeUpdate()
        }
    }
}

/**
 * Function that maps a ResultSet to a User, according to the name dictionary defined,
 *  in this case default name of Columns
 * @return The mapped User
 */
fun ResultSet.mapUser(): User =
    User(
        uid = getInt("uid").toUInt(),
        name = getString("name").toName(),
        email = getString("email").toEmail(),
        token = getString("token").toToken(),
    )
