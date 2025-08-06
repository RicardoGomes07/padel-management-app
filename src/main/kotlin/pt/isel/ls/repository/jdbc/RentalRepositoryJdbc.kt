@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import kotlinx.datetime.*
import pt.isel.ls.domain.*
import pt.isel.ls.repository.RentalRepository
import pt.isel.ls.repository.jdbc.dao.mapRentalDb
import pt.isel.ls.services.RentalError
import pt.isel.ls.services.ensureOrThrow
import pt.isel.ls.webapi.isInTheFuture
import java.sql.ResultSet
import javax.sql.DataSource

/**
 * Repository in jdbc responsible for direct interactions with the database for rentals related actions
 * @param dataSource to extract the database connection for the SQL queries
 */
class RentalRepositoryJdbc(
    private val dataSource: DataSource,
) : RentalRepository {
    /**
     * Function responsible for the creation of a club.
     * @param date Day of the rental
     * @param rentTime TimeSlot of hours that the rental will take
     * @param renterId User that is renting
     * @param courtId Court being rented
     * @return The Rental created
     * @throws RentalError.RentalDateInThePast If the date is not in the future or if the renter and/ or the court don't exist
     */
    override fun createRental(
        date: LocalDate,
        rentTime: TimeSlot,
        renterId: UInt,
        courtId: UInt,
    ): Rental =
        dataSource.connection.use { connection ->
            connection.executeMultipleQueries {
                ensureOrThrow(
                    condition = isInTheFuture(date, rentTime.start.toInt()),
                    exception = RentalError.RentalDateInThePast(date),
                )

                val sqlSelectRenter =
                    """
                    ${userSqlReturnFormat()}
                    WHERE uid = ?
                    """.trimIndent()

                val renter =
                    connection.prepareStatement(sqlSelectRenter).use { stmt ->
                        stmt.setInt(1, renterId.toInt())
                        stmt.executeQuery().use { rs ->
                            ensureOrThrow(
                                condition = rs.next(),
                                exception = RentalError.RenterNotFound(renterId),
                            )
                            rs.mapUser()
                        }
                    }

                val sqlSelectCourt =
                    """
                    ${courtSqlReturnFormat()}
                    WHERE cr.crid = ?
                    """.trimIndent()

                val court =
                    connection.prepareStatement(sqlSelectCourt).use { stmt ->
                        stmt.setInt(1, courtId.toInt())
                        stmt.executeQuery().use { rs ->
                            ensureOrThrow(
                                condition = rs.next(),
                                exception = RentalError.MissingCourt(courtId),
                            )
                            rs.mapCourt()
                        }
                    }

                val availableHours = retrieveAvailableHoursInCourtForDate(courtId, date)

                ensureOrThrow(
                    condition = (rentTime.start until rentTime.end).all { it in availableHours },
                    exception = RentalError.OverlapInTimeSlot(date, rentTime),
                )

                val sqlInsert =
                    """
                    INSERT INTO rentals (date_, rd_start, rd_end, renter_id, court_id) values (?, ?, ?, ?, ?)
                    RETURNING ${renameRentalRows()}
                    """.trimIndent()

                connection.prepareStatement(sqlInsert).use { stmt ->
                    stmt.setInt(1, date.toEpochDays())
                    stmt.setInt(2, rentTime.start.toInt())
                    stmt.setInt(3, rentTime.end.toInt())
                    stmt.setInt(4, renterId.toInt())
                    stmt.setInt(5, courtId.toInt())

                    stmt.executeQuery().use { rs ->
                        rs.next()
                        rs.mapRental(renter, court)
                    }
                }
            }
        }

    /**
     * Function that finds all available hours of a court for the given day
     * @param crid The court to look for available hours
     * @param date Day to search for available hours
     * @return The list of available hours
     */
    override fun findAvailableHoursForACourt(
        crid: UInt,
        date: LocalDate,
    ): List<UInt> =
        dataSource.connection.use { connection ->
            connection.executeMultipleQueries {
                val sqlCheckFk =
                    """
                    ${courtSqlReturnFormat()}
                    WHERE cr.crid = ?
                    """.trimIndent()

                connection.prepareStatement(sqlCheckFk).use { stmt ->
                    stmt.setInt(1, crid.toInt())

                    stmt.executeQuery().use { rs ->
                        ensureOrThrow(
                            condition = rs.next(),
                            exception = RentalError.MissingCourt(crid),
                        )
                        rs.mapCourt()
                    }
                }

                retrieveAvailableHoursInCourtForDate(crid, date)
            }
        }

    /**
     * Function that finds all rentals for a given day for a given court.
     * @param crid The court to search for rentals in
     * @param date The day to search for rentals in
     * @param limit Number of tuples to retrieve, default of 30
     * @param offset Number of tuples to skip at the beginning, default of 0
     * @return The list of rentals found
     */
    override fun findByCridAndDate(
        crid: UInt,
        date: LocalDate?,
        limit: Int,
        offset: Int,
    ): PaginationInfo<Rental> =
        dataSource.connection.use { connection ->
            connection.executeMultipleQueries {
                val sqlSelect =
                    """
                    ${rentalSqlReturnFormat()}
                    WHERE r.court_id = ?
                    ${
                        (
                            if (date != null) {
                                """ AND r.date_ = ?""".trimIndent()
                            } else {
                                ""
                            }
                        )
                    }
                    ORDER BY r.date_ ASC, r.rd_start ASC
                    LIMIT ? OFFSET ?
                    """.trimIndent()

                val rentals =
                    connection.prepareStatement(sqlSelect).use { stmt ->
                        stmt.setInt(1, crid.toInt())
                        val paramPosition =
                            if (date != null) {
                                stmt.setInt(2, date.toEpochDays())
                                3
                            } else {
                                2
                            }

                        stmt.run {
                            setInt(paramPosition, limit)
                            setInt(paramPosition + 1, offset)
                        }

                        stmt.executeQuery().use { rs ->
                            val rentals = mutableListOf<Rental>()
                            while (rs.next()) {
                                rentals.add(rs.mapRental())
                            }
                            rentals
                        }
                    }

                val sqlCount = "SELECT COUNT(*) FROM rentals WHERE court_id = ?"
                val count =
                    connection.prepareStatement(sqlCount).use { stmt ->
                        stmt.setInt(1, crid.toInt())
                        stmt.executeQuery().use { rs ->
                            if (rs.next()) rs.getInt(1) else 0
                        }
                    }

                return@executeMultipleQueries PaginationInfo(rentals, count)
            }
        }

    override fun numRentalsOfCourt(
        crid: UInt,
        date: LocalDate?,
    ): Int {
        val sql =
            if (date != null) {
                "SELECT COUNT(*) FROM rentals WHERE court_id = ? AND date_ = ?"
            } else {
                "SELECT COUNT(*) FROM rentals WHERE court_id = ?"
            }

        return dataSource.connection.use {
            it.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, crid.toInt())
                if (date != null) {
                    stmt.setObject(2, date.toEpochDays())
                }

                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.getInt(1) else 0
                }
            }
        }
    }

    /**
     * Function that finds 'limit' rentals, skipping first 'offset', of a user.
     * @param renter The User to search rentals of
     * @param limit Number of tuples to retrieve, default of 30
     * @param offset Number of tuples to skip at the beginning, default of 0
     * @return The list of rentals found
     */
    override fun findAllRentalsByRenterId(
        renter: UInt,
        limit: Int,
        offset: Int,
    ): PaginationInfo<Rental> =
        dataSource.connection.use { connection ->
            connection.executeMultipleQueries {
                val sqlSelect =
                    """
                    ${rentalSqlReturnFormat()}
                    WHERE r.renter_id = ?
                    ORDER BY r.date_ DESC, r.rd_start ASC
                    LIMIT ? OFFSET ?
                    """.trimIndent()

                val rentals =
                    connection.prepareStatement(sqlSelect).use { stmt ->
                        stmt.setInt(1, renter.toInt())
                        stmt.setInt(2, limit)
                        stmt.setInt(3, offset)

                        stmt.executeQuery().use { rs ->
                            val rentals = mutableListOf<Rental>()
                            while (rs.next()) {
                                rentals.add(rs.mapRental())
                            }
                            rentals
                        }
                    }

                val sqlCount = "SELECT COUNT(*) FROM rentals WHERE renter_id = ?"
                val count =
                    connection.prepareStatement(sqlCount).use { stmt ->
                        stmt.setInt(1, renter.toInt())
                        stmt.executeQuery().use { rs ->
                            if (rs.next()) rs.getInt(1) else 0
                        }
                    }

                return@executeMultipleQueries PaginationInfo(rentals, count)
            }
        }

    override fun numRentalsOfUser(renter: UInt): Int {
        val sql = "SELECT COUNT(*) FROM rentals WHERE renter_id = ?"
        return dataSource.connection.use {
            it.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, renter.toInt())
                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.getInt(1) else 0
                }
            }
        }
    }

    override fun updateDateAndRentTime(
        rid: UInt,
        date: LocalDate,
        rentTime: TimeSlot,
    ): Rental =
        dataSource.connection.use { connection ->
            connection.executeMultipleQueries {
                val sqlUpdate =
                    """
                    UPDATE rentals SET
                        date_ = ?,
                        rd_start = ?,
                        rd_end = ?
                    WHERE rid = ?
                    RETURNING ${renameRentalRows()}
                    """.trimIndent()

                val updatedRental =
                    connection.prepareStatement(sqlUpdate).use { stmt ->
                        stmt.setInt(1, date.toEpochDays())
                        stmt.setInt(2, rentTime.start.toInt())
                        stmt.setInt(3, rentTime.end.toInt())
                        stmt.setInt(4, rid.toInt())

                        stmt.executeQuery().use { rs ->
                            ensureOrThrow(
                                condition = rs.next(),
                                exception = RentalError.RentalUpdateFailed(rid),
                            )
                            rs.mapRentalDb()
                        }
                    }

                val sqlSelectRenter =
                    """
                    SELECT * FROM users WHERE uid = ?
                    """.trimIndent()

                val renter =
                    connection.prepareStatement(sqlSelectRenter).use { stmt ->
                        stmt.setInt(1, updatedRental.renter.toInt())
                        stmt.executeQuery().use { rs ->
                            ensureOrThrow(
                                condition = rs.next(),
                                exception = RentalError.RenterNotFound(updatedRental.renter),
                            )
                            rs.mapUser()
                        }
                    }

                val sqlSelectCourt =
                    """
                    ${courtSqlReturnFormat()}
                    WHERE cr.crid = ?
                    """.trimIndent()

                val court =
                    connection.prepareStatement(sqlSelectCourt).use { stmt ->
                        stmt.setInt(1, updatedRental.court.toInt())
                        stmt.executeQuery().use { rs ->
                            ensureOrThrow(
                                condition = rs.next(),
                                exception = RentalError.MissingCourt(updatedRental.court),
                            )
                            rs.mapCourt()
                        }
                    }

                return@executeMultipleQueries Rental(
                    rid = updatedRental.rid,
                    date = updatedRental.date,
                    rentTime = updatedRental.rentTime,
                    renter = renter,
                    court = court,
                )
            }
        }

    /**
     * Function that creates a new rental or updates, with the information given, if one with the rid already exists.
     * @param element rental to be created or updated
     */
    override fun save(element: Rental) {
        val sqlSave =
            """
            INSERT INTO rentals (date_, rd_start, rd_end, renter_id, court_id)
            VALUES (?, ?, ?, ? ,?)
            ON CONFLICT (rid)
            DO UPDATE SET
                date_ = EXCLUDED.date_,
                rd_start = EXCLUDED.rd_start,
                rd_end = EXCLUDED.rd_end,
                renter_id = EXCLUDED.renter_id,
                court_id = EXCLUDED.court_id;
            """.trimIndent()

        dataSource.connection.use {
            it.prepareStatement(sqlSave).use { stmt ->
                stmt.setInt(1, element.date.toEpochDays())
                stmt.setInt(2, element.rentTime.start.toInt())
                stmt.setInt(3, element.rentTime.end.toInt())
                stmt.setInt(4, element.renter.uid.toInt())
                stmt.setInt(5, element.court.crid.toInt())

                stmt.executeUpdate()
            }
        }
    }

    /**
     * Function that finds a rental by its rid.
     * @param id Identifier of the class, corresponding to the PK of the respective table, in this case rid
     * @return Rental if found, otherwise null
     */
    override fun findByIdentifier(id: UInt): Rental? {
        val sqlSelect =
            """
            ${rentalSqlReturnFormat()}
            WHERE r.rid = ?
            ORDER BY r.date_ DESC, r.rd_start ASC
            """.trimIndent()

        return dataSource.connection.use {
            it.prepareStatement(sqlSelect).use { stmt ->
                stmt.setInt(1, id.toInt())

                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.mapRental() else null
                }
            }
        }
    }

    /**
     * Function that returns limit elements after offset, from latest tuple to be created to oldest
     * @param limit Number of tuples to retrieve, default of 30
     * @param offset Number of tuples to skip at the beginning, default of 0
     * @return List of Rentals retrieved
     */
    override fun findAll(
        limit: Int,
        offset: Int,
    ): PaginationInfo<Rental> =
        dataSource.connection.use { connection ->
            connection.executeMultipleQueries {
                val sqlSelect =
                    """
                    ${rentalSqlReturnFormat()}
                    ORDER BY r.date_ DESC, r.rd_start ASC
                    LIMIT ? OFFSET ?
                    """.trimIndent()

                val rentals =
                    connection.prepareStatement(sqlSelect).use { stmt ->
                        stmt.setInt(1, limit)
                        stmt.setInt(2, offset)

                        stmt.executeQuery().use { rs ->
                            val rentals = mutableListOf<Rental>()
                            while (rs.next()) {
                                rentals.add(rs.mapRental())
                            }
                            rentals
                        }
                    }

                val sqlCount = "SELECT COUNT(*) FROM rentals"
                val count =
                    connection.prepareStatement(sqlCount).use { stmt ->
                        stmt.executeQuery().use { rs ->
                            if (rs.next()) rs.getInt(1) else 0
                        }
                    }

                return@executeMultipleQueries PaginationInfo(rentals, count)
            }
        }

    /**
     * Function that deletes a rental if exists a tuple with the rid, if it doesn't exist, does nothing
     * @param id Identifier of the Rental to delete
     */
    override fun deleteByIdentifier(id: UInt) {
        val sqlDelete = "DELETE FROM rentals WHERE rid = ?"

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
        val sqlDelete = "TRUNCATE TABLE rentals RESTART IDENTITY CASCADE"
        dataSource.connection.use {
            it.prepareStatement(sqlDelete).use { stmt ->
                stmt.executeUpdate()
            }
        }
    }

    private fun retrieveAvailableHoursInCourtForDate(
        crid: UInt,
        date: LocalDate,
    ): List<UInt> {
        val sqlSelect =
            """
            ${rentalSqlReturnFormat()}
            WHERE r.date_ = ? AND cr.crid = ?
            ORDER BY r.rd_start ASC
            """.trimIndent()

        val rentalsOnDate =
            dataSource.connection.use {
                it.prepareStatement(sqlSelect).use { stmt ->
                    stmt.setInt(1, date.toEpochDays())
                    stmt.setInt(2, crid.toInt())

                    stmt.executeQuery().use { rs ->
                        val rentals = mutableListOf<Rental>()
                        while (rs.next()) {
                            rentals.add(rs.mapRental())
                        }
                        rentals
                    }
                }
            }

        val hoursRange = UIntRange(0u, 23u)

        return hoursRange
            .filter { hour ->
                rentalsOnDate.none { rental ->
                    hour in rental.rentTime.start..<rental.rentTime.end
                }
            }
    }
}

/**
 * Function that returns the rows of the rentals table renamed
 * @return string renaming the rows of the table
 */
private fun renameRentalRows(alias: String = "") =
    """
    ${alias}rid as rental_id, ${alias}date_ as rental_date, ${alias}rd_start as rental_start, ${alias}rd_end as rental_end,
        ${alias}renter_id as renter_id, ${alias}court_id as court_id
    """.trimIndent()

/**
 * Function with the default select query to retrieve a rental with the information of the renter and court
 * @return the default select query string
 */
private fun rentalSqlReturnFormat() =
    """
    SELECT ${renameRentalRows("r.")},
        u.name as renter_name, u.email as renter_email, 
            u.hashed_password as renter_hashed_password, u.token as renter_token,
        cr.name as court_name,
        c.cid as court_club_id, c.name as court_club_name,
        u2.uid as court_club_owner_id, u2.name as court_club_owner_name, u2.email as court_club_owner_email, 
            u2.hashed_password as court_club_owner_hashed_password, u2.token as court_club_owner_token
    FROM rentals r
    LEFT JOIN users u ON r.renter_id = u.uid
    LEFT JOIN courts cr ON r.court_id = cr.crid
    LEFT JOIN clubs c ON cr.club_id = c.cid
    LEFT JOIN users u2 ON u2.uid = c.owner
    """.trimIndent()

/**
 * Function that maps a ResultSet to a Rental, according to the name dictionary defined,
 *  in this case the one defined in the default select query
 * @return The mapped Rental
 */
private fun ResultSet.mapRental(
    rentalName: String = "rental",
    renterName: String = "renter",
    courtName: String = "court",
    courtClubName: String = "court_club",
    courtClubOwnerName: String = "court_club_owner",
): Rental =
    Rental(
        rid = getInt("${rentalName}_id").toUInt(),
        date = LocalDate.fromEpochDays(getInt("${rentalName}_date")),
        rentTime =
            TimeSlot(
                getInt("${rentalName}_start").toUInt(),
                getInt("${rentalName}_end").toUInt(),
            ),
        renter = mapUser(renterName),
        court = mapCourt(courtName, courtClubName, courtClubOwnerName),
    )

/**
 * Function that maps a ResultSet to a Rental, according to the name dictionary defined,
 *  in this case the one defined in the default select query, doesn't read neither renter nor court through ResultSet
 * @param renter User that is renting
 * @param court Court being rented
 * @return The mapped Rental
 */
private fun ResultSet.mapRental(
    renter: User,
    court: Court,
    rentalName: String = "rental",
) = Rental(
    rid = getInt("${rentalName}_id").toUInt(),
    date = LocalDate.fromEpochDays(getInt("${rentalName}_date")),
    rentTime =
        TimeSlot(
            getInt("${rentalName}_start").toUInt(),
            getInt("${rentalName}_end").toUInt(),
        ),
    renter = renter,
    court = court,
)
