@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.domain.*
import pt.isel.ls.repository.CourtRepository
import pt.isel.ls.services.CourtError
import pt.isel.ls.services.ensureOrThrow
import java.sql.ResultSet
import javax.sql.DataSource

/**
 * Repository in jdbc responsible for direct interactions with the database for courts related actions
 * @param dataSource to extract the database connection for the SQL queries
 */
class CourtRepositoryJdbc(
    private val dataSource: DataSource,
) : CourtRepository {
    /**
     * Function responsible for the creation of a court.
     * @param name Name of the new court
     * @param clubId Identifier of the club that owns the court
     * @return The Court created
     * @throws IllegalArgumentException if clubId doesn't exist
     */
    override fun createCourt(
        name: Name,
        clubId: UInt,
    ): Court =
        dataSource.connection.use { connection ->
            connection.executeMultipleQueries {
                val sqlCheckFk =
                    """
                    ${clubSqlReturnFormat()}
                    WHERE c.cid = ?
                    """.trimIndent()

                val club =
                    connection.prepareStatement(sqlCheckFk).use { stmt ->
                        stmt.setInt(1, clubId.toInt())
                        stmt.executeQuery().use { rs ->
                            ensureOrThrow(
                                condition = rs.next(),
                                exception = CourtError.MissingClub(clubId),
                            )
                            rs.mapClub()
                        }
                    }

                val sqlInsert =
                    """
                    INSERT INTO courts (name, club_id)
                    VALUES (?, ?)
                    RETURNING ${renameCourtRows()}
                    """.trimIndent()

                val newCourt =
                    connection.prepareStatement(sqlInsert).use { stmt ->
                        stmt.setString(1, name.value)
                        stmt.setInt(2, clubId.toInt())

                        stmt.executeQuery().use { rs ->
                            ensureOrThrow(
                                condition = rs.next(),
                                exception = RuntimeException("Error inserting court"),
                            )
                            rs.mapCourt(club)
                        }
                    }
                newCourt
            }
        }

    /**
     * Function that finds all courts of a club.
     * @param cid The identifier of the club to search for the courts
     * @param limit Number of tuples to retrieve, default of 30
     * @param offset Number of tuples to skip at the beginning, default of 0
     * @return The list of courts owned by the club
     */
    override fun findByClubIdentifier(
        cid: UInt,
        limit: Int,
        offset: Int,
    ): PaginationInfo<Court> =
        dataSource.connection.use { connection ->
            connection.executeMultipleQueries {
                val sqlSelect =
                    """
                    ${courtSqlReturnFormat()}
                    WHERE cr.club_id = ?
                    ORDER BY cr.crid DESC
                    LIMIT ? OFFSET ?
                    """.trimIndent()

                val courts =
                    connection.prepareStatement(sqlSelect).use { stmt ->
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

                val sqlCount = "SELECT COUNT(*) FROM courts where club_id = ?"
                val count =
                    connection.prepareStatement(sqlCount).use { stmt ->
                        stmt.setInt(1, cid.toInt())
                        stmt.executeQuery().use { rs ->
                            if (rs.next()) rs.getInt(1) else 0
                        }
                    }
                return@executeMultipleQueries PaginationInfo(courts, count)
            }
        }

    /**
     * Function that creates a new court or updates, with the information given, if one with the crid already exists.
     * @param element court to be created or updated
     */
    override fun save(element: Court) {
        val sqlSave =
            """
            INSERT INTO courts (name, club_id)
            VALUES (?, ?)
            ON CONFLICT (crid)
            DO UPDATE SET
                name = EXCLUDED.name,
                club_id = EXCLUDED.club_id;
            """.trimIndent()

        dataSource.connection.use {
            it.prepareStatement(sqlSave).use { stmt ->
                stmt.setString(1, element.name.value)
                stmt.setInt(2, element.club.cid.toInt())

                stmt.executeUpdate()
            }
        }
    }

    /**
     * Function that finds a court by its crid.
     * @param id Identifier of the class, corresponding to the PK of the respective table, in this case crid
     * @return Court if found, otherwise null
     */
    override fun findByIdentifier(id: UInt): Court? {
        val sqlSelect =
            """
            ${courtSqlReturnFormat()}
            WHERE cr.crid = ?
            """.trimIndent()

        return dataSource.connection.use {
            it.prepareStatement(sqlSelect).use { stmt ->
                stmt.setInt(1, id.toInt())

                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.mapCourt() else null
                }
            }
        }
    }

    /**
     * Function that returns limit elements after offset, from latest tuple to be created to oldest
     * @param limit Number of tuples to retrieve, default of 30
     * @param offset Number of tuples to skip at the beginning, default of 0
     * @return List of Courts retrieved
     */
    override fun findAll(
        limit: Int,
        offset: Int,
    ): PaginationInfo<Court> =
        dataSource.connection.use { connection ->
            connection.executeMultipleQueries {
                val sqlSelect =
                    """
                    ${courtSqlReturnFormat()}
                    ORDER BY cr.crid DESC
                    LIMIT ? OFFSET ?
                    """.trimIndent()

                val courts =
                    connection.prepareStatement(sqlSelect).use { stmt ->
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

                val sqlCount = "SELECT COUNT(*) FROM courts"
                val count =
                    connection.prepareStatement(sqlCount).use { stmt ->
                        stmt.executeQuery().use { rs ->
                            if (rs.next()) rs.getInt(1) else 0
                        }
                    }

                return@executeMultipleQueries PaginationInfo(courts, count)
            }
        }

    /**
     * Function that deletes a court if exists a tuple with the crid, if it doesn't exist, does nothing
     * @param id Identifier of the Court to delete
     */
    override fun deleteByIdentifier(id: UInt) {
        val sqlDelete = "DELETE FROM courts WHERE crid = ?"

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
        val sqlDelete = "TRUNCATE TABLE courts RESTART IDENTITY CASCADE"
        dataSource.connection.use {
            it.prepareStatement(sqlDelete).use { stmt ->
                stmt.executeUpdate()
            }
        }
    }

    override fun count(cid: UInt): Int {
        val sqlCount = "SELECT COUNT(*) FROM courts where club_id = ?"
        return dataSource.connection.use {
            it.prepareStatement(sqlCount).use { stmt ->
                stmt.setInt(1, cid.toInt())
                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.getInt(1) else 0
                }
            }
        }
    }
}

/**
 * Function that returns the rows of the courts table renamed
 * @return string renaming the rows of the table
 */
private fun renameCourtRows(alias: String = "") =
    """
    ${alias}crid as court_id, ${alias}name as court_name, ${alias}club_id as club_id
    """.trimIndent()

/**
 * Function with the default select query to retrieve a court with the information of the club
 * @return the default select query string
 */
fun courtSqlReturnFormat() =
    """
    SELECT ${renameCourtRows("cr.")},
        c.name as club_name, c.owner as club_owner_id,
        u.name as club_owner_name, u.email as club_owner_email, u.hashed_password as club_owner_hashed_password, u.token as club_owner_token
    FROM courts cr
    LEFT JOIN clubs c ON cr.club_id = c.cid
    LEFT JOIN users u ON c.owner = u.uid
    """.trimIndent()

/**
 * Function that maps a ResultSet to a Court, according to the name dictionary defined,
 *  in this case the one defined in the default select query
 * @return The mapped Court
 */
fun ResultSet.mapCourt(
    courtName: String = "court",
    clubName: String = "club",
    clubOwnerName: String = "club_owner",
): Court =
    Court(
        crid = getInt("${courtName}_id").toUInt(),
        name = getString("${courtName}_name").toName(),
        club = mapClub(clubName, clubOwnerName),
    )

/**
 * Function that maps a ResultSet to a Court, according to the name dictionary defined,
 *  in this case the one defined in the default select query, doesn't read club through ResultSet
 * @param club Club
 * @return The mapped Court
 */
fun ResultSet.mapCourt(
    club: Club,
    courtName: String = "court",
) = Court(
    crid = getInt("${courtName}_id").toUInt(),
    name = getString("${courtName}_name").toName(),
    club = club,
)
