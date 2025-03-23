@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.domain.*
import pt.isel.ls.repository.ClubRepository
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class ClubRepositoryJdbc(
    private val connection: Connection,
) : ClubRepository {
    override fun createClub(
        name: Name,
        ownerId: UInt,
    ): Club {
        val sqlInsert =
            """
            WITH inserted ic AS (
                INSERT INTO clubs (name, owner) values (?, ?)
                RETURNING *
            )
            SELECT *
            FROM inserted ic
            LEFT JOIN users u ON ic.owner = u.uid
            """.trimIndent()

        return connection.prepareStatement(sqlInsert).use { stmt ->
            stmt.setString(1, name.value)
            stmt.setInt(2, ownerId.toInt())

            stmt.executeQuery().use { rs ->
                if (rs.next()) {
                    rs.mapClub()
                } else {
                    throw SQLException("User creation failed, no ID obtained.")
                }
            }
        }
    }

    override fun findClubByName(name: Name): Club? {
        val sqlSelect =
            """
            SELECT * FROM clubs ic
            LEFT JOIN users u ON ic.owner = u.uid
            """.trimIndent()

        return connection.prepareStatement(sqlSelect).use { stmt ->
            stmt.setString(1, name.value)

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.mapClub() else null
            }
        }
    }

    override fun save(element: Club) {
        val sqlUpdate =
            """
            UPDATE users
            SET name = ?, owner = ?
            WHERE cid = ?
            """.trimIndent()

        connection.prepareStatement(sqlUpdate).use { stmt ->
            stmt.setString(1, element.name.value)
            stmt.setInt(2, element.owner.uid.toInt())
            stmt.setInt(3, element.cid.toInt())

            // if no row was updated, there is no such user, so create one
            if (stmt.executeUpdate() == 0) {
                createClub(element.name, element.owner.uid)
            }
        }
    }

    override fun findByIdentifier(id: UInt): Club? {
        val sqlSelect =
            """
            SELECT * FROM clubs ic WHERE ic.cid = ?
            LEFT JOIN users u ON ic.owner = u.uid
            """.trimIndent()

        return connection.prepareStatement(sqlSelect).use { stmt ->
            stmt.setInt(1, id.toInt())

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.mapClub() else null
            }
        }
    }

    override fun findAll(
        limit: Int,
        offset: Int,
    ): List<Club> {
        val sqlSelect =
            """
            SELECT * FROM clubs ic
            LEFT JOIN users u ON ic.owner = u.uid
            ORDER BY ic.cid DESC
            LIMIT ? OFFSET ?
            """.trimIndent()

        return connection.prepareStatement(sqlSelect).use { stmt ->
            stmt.setInt(1, limit)
            stmt.setInt(2, offset)

            stmt.executeQuery().use { rs ->
                val clubs = mutableListOf<Club>()
                while (rs.next()) {
                    clubs.add(rs.mapClub())
                }
                clubs
            }
        }
    }

    override fun deleteByIdentifier(id: UInt) {
        val sqlDelete = "DELETE FROM clubs ic WHERE cid = ?"

        connection.prepareStatement(sqlDelete).use { stmt ->
            stmt.setInt(1, id.toInt())
            stmt.executeUpdate()
        }
    }

    override fun clear() {
        val sqlDelete = "TRUNCATE TABLE clubs RESTART IDENTITY CASCADE"
        connection.prepareStatement(sqlDelete).use { stmt ->
            stmt.executeUpdate()
        }
    }

    private fun ResultSet.mapClub() =
        Club(
            cid = getInt("ic.cid").toUInt(),
            name = Name(getString("ic.name")),
            owner =
                User(
                    uid = getInt("u.uid").toUInt(),
                    name = Name(getString("u.name")),
                    email = Email(getString("u.email")),
                    token = getString("u.token").toToken(),
                ),
        )
}
