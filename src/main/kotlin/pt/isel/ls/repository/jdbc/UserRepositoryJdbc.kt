@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.domain.*
import pt.isel.ls.repository.UserRepository
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class UserRepositoryJdbc(
    private val connection: Connection,
) : UserRepository {
    override fun createUser(
        name: Name,
        email: Email,
    ): User {
        val token = generateToken()

        val sqlInsert = "INSERT INTO users (name, email, token) VALUES (?, ?, ?) RETURNING *"

        return connection.prepareStatement(sqlInsert).use { stmt ->
            stmt.setString(1, name.value)
            stmt.setString(2, email.value)
            stmt.setString(3, token.toString())

            stmt.executeQuery().use { rs ->
                if (rs.next()) {
                    rs.mapUser()
                } else {
                    throw SQLException("User creation failed, no ID obtained.")
                }
            }
        }
    }

    override fun findUserByToken(token: Token): User? {
        val sqlSelect = "SELECT * FROM users WHERE token = ?"

        return connection.prepareStatement(sqlSelect).use { stmt ->
            stmt.setString(1, token.toString())
            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.mapUser() else null
            }
        }
    }

    override fun save(element: User) {
        val sqlUpdate =
            """
            UPDATE users
            SET name = ?, email = ?, token = ?
            WHERE uid = ?
            """.trimIndent()

        connection.prepareStatement(sqlUpdate).use { stmt ->
            stmt.setString(1, element.name.value)
            stmt.setString(2, element.email.value)
            stmt.setString(3, element.token.toString())
            stmt.setInt(4, element.uid.toInt())

            // if no row was updated, there is no such user, so create one
            if (stmt.executeUpdate() == 0) {
                createUser(element.name, element.email)
            }
        }
    }

    override fun findByIdentifier(id: UInt): User? {
        val sqlSelect = "SELECT * FROM users WHERE uid = ?"

        return connection.prepareStatement(sqlSelect).use { stmt ->
            stmt.setInt(1, id.toInt())

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.mapUser() else null
            }
        }
    }

    override fun findAll(
        limit: Int,
        offset: Int,
    ): List<User> {
        val sqlSelect =
            """
            SELECT * FROM users
            ORDER BY uid DESC
            LIMIT ? OFFSET ?
            """.trimIndent()

        return connection.prepareStatement(sqlSelect).use { stmt ->
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
    }

    override fun deleteByIdentifier(id: UInt) {
        val sqlDelete = "DELETE FROM users WHERE uid = ?"

        connection.prepareStatement(sqlDelete).use { stmt ->
            stmt.setInt(1, id.toInt())
            stmt.executeUpdate()
        }
    }

    override fun clear() {
        val sqlDelete = "TRUNCATE TABLE users RESTART IDENTITY CASCADE"
        connection.prepareStatement(sqlDelete).use { stmt ->
            stmt.executeUpdate()
        }
    }

    // Mapper for User
    private fun ResultSet.mapUser(): User =
        User(
            uid = getInt("uid").toUInt(),
            name = Name(getString("name")),
            email = Email(getString("email")),
            token = getString("token").toToken(),
        )
}
