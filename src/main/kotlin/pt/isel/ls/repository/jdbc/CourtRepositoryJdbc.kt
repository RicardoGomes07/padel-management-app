@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.domain.*
import pt.isel.ls.repository.CourtRepository
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class CourtRepositoryJdbc(
    private val connection: Connection
) : CourtRepository {
    override fun createCourt(
        name: Name,
        clubId: UInt,
    ): Court {
        val sqlInsert =
            """
            WITH inserted icr AS (
                INSERT INTO courts (name, club_id) values (?, ?)
                RETURNING *
            )
            SELECT *
            FROM inserted icr
            LEFT JOIN clubs c ON icr.club_id = c.cid
            LEFT JOIN users u ON c.owner = u.uid
            """.trimIndent()

        return connection.prepareStatement(sqlInsert).use { stmt ->
            stmt.setString(1, name.value)
            stmt.setInt(2, clubId.toInt())

            stmt.executeQuery().use { rs ->
                if (rs.next()) {
                    rs.mapCourt()
                } else {
                    throw SQLException("User creation failed, no ID obtained.")
                }
            }
        }
    }

    override fun findByClubIdentifier(
        cid: UInt,
        limit: Int,
        offset: Int,
    ): List<Court> {
        val sqlSelect =
            """
            SELECT * FROM courts icr WHERE icr.club_id = ?
            LEFT JOIN clubs c ON icr.club_id = c.cid
            LEFT JOIN users u ON c.owner = u.uid
            ORDER BY icr.crid DESC
            LIMIT ? OFFSET ?
            """.trimIndent()

        return connection.prepareStatement(sqlSelect).use { stmt ->
            stmt.setInt(1, cid.toInt())
            stmt.setInt(2, limit)
            stmt.setInt(3, offset)

            stmt.executeQuery().use { rs ->
                val courts = mutableListOf<Court>()
                while (rs.next()) {
                    courts.add(rs.mapCourt())
                }
                courts
            }
        }
    }

    override fun save(element: Court) {
        val sqlUpdate =
            """
            UPDATE users
            SET name = ?, club_id = ?
            WHERE uid = ?
            """.trimIndent()

        connection.prepareStatement(sqlUpdate).use { stmt ->
            stmt.setString(1, element.name.value)
            stmt.setInt(2, element.club.cid.toInt())
            stmt.setInt(3, element.crid.toInt())

            // if no row was updated, there is no such user, so create one
            if (stmt.executeUpdate() == 0) {
                createCourt(element.name, element.club.cid)
            }
        }
    }

    override fun findByIdentifier(id: UInt): Court? {
        val sqlSelect =
            """
            SELECT * FROM courts icr WHERE icr.crid = ?
            LEFT JOIN clubs c ON icr.club_id = c.cid
            LEFT JOIN users u ON c.owner = u.uid
            """.trimIndent()

        return connection.prepareStatement(sqlSelect).use { stmt ->
            stmt.setInt(1, id.toInt())

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.mapCourt() else null
            }
        }
    }

    override fun findAll(
        limit: Int,
        offset: Int,
    ): List<Court> {
        val sqlSelect =
            """
            SELECT * FROM courts icr
            LEFT JOIN clubs c ON icr.club_id = c.cid
            LEFT JOIN users u ON c.owner = u.uid
            ORDER BY icr.crid DESC
            LIMIT ? OFFSET ?
            """.trimIndent()

        return connection.prepareStatement(sqlSelect).use { stmt ->
            stmt.setInt(1, limit)
            stmt.setInt(2, offset)

            stmt.executeQuery().use { rs ->
                val courts = mutableListOf<Court>()
                while (rs.next()) {
                    courts.add(rs.mapCourt())
                }
                courts
            }
        }
    }

    override fun deleteByIdentifier(id: UInt) {
        val sqlDelete = "DELETE FROM courts WHERE crid = ?"

        connection.prepareStatement(sqlDelete).use { stmt ->
            stmt.setInt(1, id.toInt())
            stmt.executeUpdate()
        }
    }

    override fun clear() {
        val sqlDelete = "TRUNCATE TABLE courts RESTART IDENTITY CASCADE"
        connection.prepareStatement(sqlDelete).use { stmt ->
            stmt.executeUpdate()
        }
    }

    private fun ResultSet.mapCourt(): Court =
        Court(
            crid = getInt("icr.crid").toUInt(),
            name = Name(getString("icr.name")),
            club =
                Club(
                    cid = getInt("c.cid").toUInt(),
                    name = Name(getString("c.name")),
                    owner =
                        User(
                            uid = getInt("u.uid").toUInt(),
                            name = Name(getString("u.name")),
                            email = Email(getString("u.email")),
                            token = getString("u.token").toToken(),
                        ),
                ),
        )
}