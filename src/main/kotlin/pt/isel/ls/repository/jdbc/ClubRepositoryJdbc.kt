@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.domain.*
import pt.isel.ls.repository.ClubRepository
import pt.isel.ls.services.ClubError
import pt.isel.ls.services.ensureOrThrow
import java.sql.Connection
import java.sql.ResultSet

/**
 * Repository in jdbc responsible for direct interactions with the database for clubs related actions
 * @param connection The database connection for the SQL queries
 */
class ClubRepositoryJdbc(
    private val connection: Connection,
) : ClubRepository {
    /**
     * Function responsible for the creation of a club.
     * @param name Name of the new club
     * @param ownerId Identifier of the user that owns the club
     * @return The Club created
     * @throws IllegalArgumentException if either the ownerId doesn't exist or the name is not unique
     */
    override fun createClub(
        name: Name,
        ownerId: UInt,
    ): Club =
        connection.executeMultipleQueries {
            val sqlCheckFK =
                """
                SELECT * FROM users u WHERE u.uid = ?
                """.trimIndent()

            val owner =
                connection.prepareStatement(sqlCheckFK).use { stmt ->
                    stmt.setInt(1, ownerId.toInt())
                    stmt.executeQuery().use { rs ->
                        ensureOrThrow(
                            condition = rs.next(),
                            exception = ClubError.OwnerNotFound(ownerId),
                        )
                        rs.mapUser()
                    }
                }

            val sqlInsert =
                """
                INSERT INTO clubs (name, owner)
                VALUES (?, ?)
                ON CONFLICT (name) DO NOTHING
                RETURNING cid AS club_id, name AS club_name, owner AS owner_id
                """.trimIndent()

            connection.prepareStatement(sqlInsert).use { stmt ->
                stmt.setString(1, name.value)
                stmt.setInt(2, ownerId.toInt())

                stmt.executeQuery().use { rs ->
                    ensureOrThrow(
                        condition = rs.next(),
                        exception = ClubError.ClubAlreadyExists(name.value),
                    )
                    rs.mapClub(owner)
                }
            }
        }

    override fun findClubsByName(
        name: Name,
        limit: Int,
        offset: Int,
    ): PaginationInfo<Club> =
        connection.executeMultipleQueries {
            val sqlSelect =
                """
                ${clubSqlReturnFormat()}
                WHERE c.name ILIKE ?
                ORDER BY c.cid DESC
                LIMIT ? OFFSET ?
                """.trimIndent()

            val clubs =
                connection.prepareStatement(sqlSelect).use { stmt ->
                    stmt.setString(1, "%${name.value}%")
                    stmt.setInt(2, limit)
                    stmt.setInt(3, offset)

                    stmt.executeQuery().use { rs ->
                        val clubs = mutableListOf<Club>()
                        while (rs.next()) {
                            clubs.add(rs.mapClub())
                        }
                        clubs
                    }
                }

            val sqlCount = "SELECT COUNT(*) FROM clubs WHERE name ILIKE ?"
            val count =
                connection.prepareStatement(sqlCount).use { stmt ->
                    stmt.setString(1, "%${name.value}%")
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) rs.getInt(1) else 0
                    }
                }

            return@executeMultipleQueries PaginationInfo(clubs, count)
        }

    /**
     * Function that creates a new club or updates, with the information given, if one with the cid already exists.
     * @param element club to be created or updated
     */
    override fun save(element: Club) {
        val sqlSave =
            """
            INSERT INTO clubs (name, owner)
            VALUES (?, ?)
            ON CONFLICT (cid)
            DO UPDATE SET
                name = EXCLUDED.name,
                owner = EXCLUDED.owner;
            """.trimIndent()

        connection.prepareStatement(sqlSave).use { stmt ->
            stmt.setString(1, element.name.value)
            stmt.setInt(2, element.owner.uid.toInt())

            stmt.executeUpdate()
        }
    }

    /**
     * Function that finds a club by its cid.
     * @param id Identifier of the class, corresponding to the PK of the respective table, in this case cid
     * @return Club if found, otherwise null
     */
    override fun findByIdentifier(id: UInt): Club? =
        connection.executeMultipleQueries {
            val sqlSelect =
                """
                ${clubSqlReturnFormat()}
                WHERE c.cid = ?
                """.trimIndent()

            connection.prepareStatement(sqlSelect).use { stmt ->
                stmt.setInt(1, id.toInt())

                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.mapClub() else return@executeMultipleQueries null
                }
            }
        }

    /**
     * Function that returns limit elements after offset, from latest tuple to be created to oldest
     * @param limit Number of tuples to retrieve, default of 30
     * @param offset Number of tuples to skip at the beginning, default of 0
     * @return List of Clubs retrieved
     */
    override fun findAll(
        limit: Int,
        offset: Int,
    ): PaginationInfo<Club> =
        connection.executeMultipleQueries {
            val sqlSelect =
                """
                ${clubSqlReturnFormat()}
                ORDER BY c.cid DESC
                LIMIT ? OFFSET ?
                """.trimIndent()

            val clubs =
                connection.prepareStatement(sqlSelect).use { stmt ->
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

            val count = count()

            return@executeMultipleQueries PaginationInfo(clubs, count)
        }

    /**
     * Function that deletes a club if exists a tuple with the cid, if it doesn't exist, does nothing
     * @param id Identifier of the Club to delete
     */
    override fun deleteByIdentifier(id: UInt) {
        val sqlDelete = "DELETE FROM clubs c WHERE c.cid = ?"

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
        val sqlDelete = "TRUNCATE TABLE clubs RESTART IDENTITY CASCADE"
        connection.prepareStatement(sqlDelete).use { stmt ->
            stmt.executeUpdate()
        }
    }

    override fun count(): Int {
        val sqlCount = "SELECT COUNT(*) FROM clubs"
        return connection.prepareStatement(sqlCount).use { stmt ->
            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.getInt(1) else 0
            }
        }
    }
}

/**
 * Function with the default select query to retrieve a club with the information of the owner
 * @return the default select query string
 */
fun clubSqlReturnFormat() =
    """
    SELECT c.cid as club_id, c.name as club_name, c.owner as owner_id,
        u.name as owner_name, u.email as owner_email, u.hashed_password as owner_hashed_password, u.token as owner_token
    FROM clubs c
    LEFT JOIN users u ON u.uid = c.owner
    """.trimIndent()

/**
 * Function that maps a ResultSet to a Club, according to the name dictionary defined,
 *  in this case the one defined in the default select query
 * @return The mapped Club
 */
fun ResultSet.mapClub() =
    Club(
        cid = getInt("club_id").toUInt(),
        name = Name(getString("club_name")),
        owner =
            User(
                uid = getInt("owner_id").toUInt(),
                name = Name(getString("owner_name")),
                email = Email(getString("owner_email")),
                password = getString("owner_hashed_password").toPassword(),
                token = getString("owner_token")?.toToken(),
            ),
    )

/**
 * Function that maps a ResultSet to a Club, according to the name dictionary defined,
 *  in this case the one defined in the default select query, doesn't read owner through ResultSet
 * @param owner User correspondent to the owner
 * @return The mapped Club
 */
fun ResultSet.mapClub(owner: User) =
    Club(
        cid = getInt("club_id").toUInt(),
        name = Name(getString("club_name")),
        owner = owner,
    )
