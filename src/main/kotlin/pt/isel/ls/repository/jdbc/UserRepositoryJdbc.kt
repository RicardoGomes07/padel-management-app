@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.domain.*
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.services.UserError
import pt.isel.ls.services.ensureOrThrow
import java.sql.ResultSet
import javax.sql.DataSource

/**
 * Repository in jdbc responsible for direct interactions with the database for users related actions
 * @param connection The database connection for the SQL queries
 */
class UserRepositoryJdbc(
    private val dataSource: DataSource,
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
        password: Password,
    ): User {
        val sqlInsert =
            """
            INSERT INTO users (name, email, hashed_password)
            VALUES (?, ?, ?)
            ON CONFLICT (email) DO NOTHING
            RETURNING ${renameUserRows()};
            """.trimIndent()

        return dataSource.connection.use {
            it.prepareStatement(sqlInsert).use { stmt ->
                stmt.setString(1, name.value)
                stmt.setString(2, email.value)
                stmt.setString(3, password.value)

                stmt.executeQuery().use { rs ->
                    ensureOrThrow(
                        condition = rs.next(),
                        exception = UserError.UserAlreadyExists(email.value),
                    )
                    rs.mapUser()
                }
            }
        }
    }

    override fun login(
        email: Email,
        password: Password,
    ): User =
        dataSource.connection.use { connection ->
            connection.executeMultipleQueries {
                val sqlSelect =
                    """
                    ${userSqlReturnFormat()}
                    WHERE u.email = ?
                    """.trimIndent()

                val user =
                    connection.prepareStatement(sqlSelect).use { stmt ->
                        stmt.setString(1, email.value)
                        stmt.executeQuery().use { rs ->
                            if (rs.next()) rs.mapUser() else null
                        }
                    }
                requireNotNull(user)

                require(password == user.password)

                val token = generateToken()

                val sqlUpdate = "UPDATE users SET token = ? WHERE email = ? RETURNING ${renameUserRows()}"

                return@executeMultipleQueries connection.prepareStatement(sqlUpdate).use { stmt ->
                    stmt.setString(1, token.toString())
                    stmt.setString(2, email.value)
                    stmt.executeQuery().use { rs ->
                        ensureOrThrow(
                            condition = rs.next(),
                            exception = UserError.UserFailedLogin(),
                        )

                        rs.mapUser()
                    }
                }
            }
        }

    override fun logout(email: Email) {
        val sqlUpdate = "UPDATE users SET token = NULL WHERE email = ?"

        dataSource.connection.use {
            it.prepareStatement(sqlUpdate).use { stmt ->
                stmt.setString(1, email.value)
                stmt.executeUpdate()
            }
        }
    }

    /**
     * Function that finds a User by a given token.
     * @param token The token to search respective user
     * @return The respective User if found, otherwise null
     */
    override fun findUserByToken(token: Token): User? {
        val sqlSelect =
            """
            ${userSqlReturnFormat()}
            WHERE u.token = ?
            """

        return dataSource.connection.use {
            it.prepareStatement(sqlSelect).use { stmt ->
                stmt.setString(1, token.toString())
                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.mapUser() else null
                }
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
            INSERT INTO users (name, email, hashed_password, token)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (email)
            DO UPDATE SET
                name = EXCLUDED.name,
                email = EXCLUDED.email,
                hashed_password = EXCLUDED.hashed_password,
                token = EXCLUDED.token;
            """.trimIndent()

        dataSource.connection.use {
            it.prepareStatement(sqlSave).use { stmt ->
                stmt.setString(1, element.name.value)
                stmt.setString(2, element.email.value)
                stmt.setString(3, element.password.value)
                stmt.setString(4, element.token.toString())

                stmt.executeUpdate()
            }
        }
    }

    /**
     * Function that finds a User by its uid.
     * @param id Identifier of the class, corresponding to the PK of the respective table, in this case uid
     * @return User if found, otherwise null
     */
    override fun findByIdentifier(id: UInt): User? {
        val sqlSelect =
            """
            ${userSqlReturnFormat()}
            WHERE u.uid = ?
            """.trimIndent()

        return dataSource.connection.prepareStatement(sqlSelect).use { stmt ->
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
        dataSource.connection.use { connection ->
            connection.executeMultipleQueries {
                val sqlSelect =
                    """
                    ${userSqlReturnFormat()}
                    ORDER BY u.uid DESC
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
        }

    /**
     * Function that deletes a User if exists a tuple with the uid, if it doesn't exist, does nothing
     * @param id Identifier of the User to delete
     */
    override fun deleteByIdentifier(id: UInt) {
        val sqlDelete = "DELETE FROM users WHERE uid = ?"

        dataSource.connection.use {
            it.prepareStatement(sqlDelete).use { stmt ->
                stmt.setInt(1, id.toInt())
                stmt.executeUpdate()
            }
        }
    }

    /**
     * Function that deletes every entry of the table,
     *  resets autoincremented values and any rows that have references to it
     */
    override fun clear() {
        val sqlDelete = "TRUNCATE TABLE users RESTART IDENTITY CASCADE"
        dataSource.connection.use {
            it.prepareStatement(sqlDelete).use { stmt ->
                stmt.executeUpdate()
            }
        }
    }
}

/**
 * Function that returns the rows of the table renamed
 * @return string renaming the rows of the table
 */
private fun renameUserRows(alias: String = "") =
    """
    ${alias}uid as _id, ${alias}name as _name, ${alias}email as _email,
        ${alias}hashed_password as _hashed_password, ${alias}token as _token 
    """.trimIndent()

/**
 * Function with the default select query to retrieve a user
 * @return the default select query string
 */
fun userSqlReturnFormat() =
    """
    SELECT ${renameUserRows("u.")}
    FROM users u
    """.trimIndent()

/**
 * Function that maps a ResultSet to a User, according to the name dictionary defined,
 *  in this case default name of Columns
 * @return The mapped User
 */
fun ResultSet.mapUser(userName: String = ""): User =
    User(
        uid = getInt("${userName}_id").toUInt(),
        name = getString("${userName}_name").toName(),
        email = getString("${userName}_email").toEmail(),
        password = getString("${userName}_hashed_password").toPassword(),
        token = getString("${userName}_token")?.takeIf { it != "null" }?.toToken(),
    )
