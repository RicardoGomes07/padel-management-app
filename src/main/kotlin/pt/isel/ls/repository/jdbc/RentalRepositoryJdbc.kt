@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import kotlinx.datetime.LocalDate
import pt.isel.ls.domain.*
import pt.isel.ls.repository.RentalRepository
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class RentalRepositoryJdbc(
    private val connection: Connection,
) : RentalRepository {
    override fun createRental(
        date: LocalDate,
        rentTime: TimeSlot,
        renterId: UInt,
        courtId: UInt,
    ): Rental {
        val sqlInsert =
            """
            WITH inserted ir AS (
                INSERT INTO rentals (date_, rd_start, rd_end, renter_id, court_id) values (?, ?, ?, ?, ?)
                RETURNING *
            )
            SELECT *
            FROM inserted ir
            LEFT JOIN users u WHERE ir.renter_id = u.uid
            LEFT JOIN courts cr WHERE ir.court_id = cr.crid
            LEFT JOIN clubs c WHERE cr.club_id = c.cid
            """.trimIndent()

        return connection.prepareStatement(sqlInsert).use { stmt ->
            stmt.setInt(1, date.toEpochDays())
            stmt.setInt(2, rentTime.start.toInt())
            stmt.setInt(3, rentTime.end.toInt())
            stmt.setInt(4, renterId.toInt())
            stmt.setInt(5, courtId.toInt())

            stmt.executeQuery().use { rs ->
                if (rs.next()) {
                    rs.mapRental()
                } else {
                    throw SQLException("User creation failed, no ID obtained.")
                }
            }
        }
    }

    override fun findAvailableHoursForACourt(
        crid: UInt,
        date: LocalDate,
    ): List<UInt> {
        val sqlSelect =
            """
            SELECT * FROM rentals ir WHERE ir.date = ?
            LEFT JOIN users u WHERE ir.renter_id = u.uid
            LEFT JOIN courts cr WHERE ir.court_id = cr.crid
            LEFT JOIN clubs c WHERE cr.club_id = c.cid
            ORDER BY ir.rd_start ASC
            """.trimIndent()

        val rentalsOnDate =
            connection.prepareStatement(sqlSelect).use { stmt ->
                stmt.setInt(1, date.toEpochDays())

                stmt.executeQuery().use { rs ->
                    val rentals = mutableListOf<Rental>()
                    while (rs.next()) {
                        rentals.add(rs.mapRental())
                    }
                    rentals
                }
            }

        val hoursRange = UIntRange(0u, 23u)

        return hoursRange
            .filter { hour ->
                rentalsOnDate.none { rental ->
                    hour in rental.rentTime.start..rental.rentTime.end
                }
            }
    }

    override fun findByCridAndDate(
        crid: UInt,
        date: LocalDate?,
        limit: Int,
        offset: Int,
    ): List<Rental> {
        val sqlSelect =

            """
            SELECT * FROM rentals ir WHERE ir.crid = ?
            """ +
                (
                    if (date != null) {
                        """
                        AND ir.date_ = ?
                        """.trimIndent()
                    } else {
                        ""
                    }
                ) +
                """
                LEFT JOIN users u WHERE ir.renter_id = u.uid
                LEFT JOIN courts cr WHERE ir.court_id = cr.crid
                LEFT JOIN clubs c WHERE cr.club_id = c.cid
                ORDER BY ir.date ASC, ir.rd_start ASC
                LIMIT ? OFFSET ?
                """.trimIndent()

        return connection.prepareStatement(sqlSelect).use { stmt ->
            stmt.setInt(1, crid.toInt())
            val paramPosition =
                if (date != null) {
                    stmt.setInt(2, date.toEpochDays())
                    3
                } else {
                    2
                }

            stmt.setInt(paramPosition, limit)
            stmt.setInt(paramPosition + 1, offset)

            stmt.executeQuery().use { rs ->
                val rentals = mutableListOf<Rental>()
                while (rs.next()) {
                    rentals.add(rs.mapRental())
                }
                rentals
            }
        }
    }

    override fun findAllRentalsByRenterId(
        renter: UInt,
        limit: Int,
        offset: Int,
    ): List<Rental> {
        val sqlSelect =
            """
            SELECT * FROM rentals ir WHERE ir.renter_id = ?
            LEFT JOIN users u WHERE ir.renter_id = u.uid
            LEFT JOIN courts cr WHERE ir.court_id = cr.crid
            LEFT JOIN clubs c WHERE cr.club_id = c.cid
            ORDER BY ir.date DESC, ir.rd_start ASC
            LIMIT ? OFFSET ?
            """.trimIndent()

        return connection.prepareStatement(sqlSelect).use { stmt ->
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
    }

    override fun save(element: Rental) {
        val sqlUpdate =
            """
            UPDATE rentals
            SET date_ = ?, rd_start = ?, rd_end = ?, renter_id = ?, court_id = ?
            WHERE uid = ?
            """.trimIndent()

        connection.prepareStatement(sqlUpdate).use { stmt ->
            stmt.setInt(1, element.date.toEpochDays())
            stmt.setInt(2, element.rentTime.start.toInt())
            stmt.setInt(3, element.rentTime.end.toInt())
            stmt.setInt(4, element.renter.uid.toInt())
            stmt.setInt(5, element.court.crid.toInt())

            // if no row was updated, there is no such user, so create one
            if (stmt.executeUpdate() == 0) {
                createRental(element.date, element.rentTime, element.renter.uid, element.court.crid)
            }
        }
    }

    override fun findByIdentifier(id: UInt): Rental? {
        val sqlSelect =
            """
            SELECT * FROM rentals ir WHERE ir.rid = ?
            LEFT JOIN users u WHERE ir.renter_id = u.uid
            LEFT JOIN courts cr WHERE ir.court_id = cr.crid
            LEFT JOIN clubs c WHERE cr.club_id = c.cid
            ORDER BY ir.date DESC, ir.rd_start ASC
            """.trimIndent()

        return connection.prepareStatement(sqlSelect).use { stmt ->
            stmt.setInt(1, id.toInt())

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.mapRental() else null
            }
        }
    }

    override fun findAll(
        limit: Int,
        offset: Int,
    ): List<Rental> {
        val sqlSelect =
            """
            SELECT * FROM rentals ir
            LEFT JOIN users u WHERE ir.renter_id = u.uid
            LEFT JOIN courts cr WHERE ir.court_id = cr.crid
            LEFT JOIN clubs c WHERE cr.club_id = c.cid
            ORDER BY ir.date DESC, ir.rd_start ASC
            LIMIT ? OFFSET ?
            """.trimIndent()

        return connection.prepareStatement(sqlSelect).use { stmt ->
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
    }

    override fun deleteByIdentifier(id: UInt) {
        val sqlDelete = "DELETE FROM rentals WHERE rid = ?"

        connection.prepareStatement(sqlDelete).use { stmt ->
            stmt.setInt(1, id.toInt())
            stmt.executeUpdate()
        }
    }

    override fun clear() {
        val sqlDelete = "TRUNCATE TABLE rentals RESTART IDENTITY CASCADE"
        connection.prepareStatement(sqlDelete).use { stmt ->
            stmt.executeUpdate()
        }
    }

    private fun ResultSet.mapRental(): Rental =
        Rental(
            rid = getInt("ir.rid").toUInt(),
            date = LocalDate.fromEpochDays(getInt("ir.date_")),
            rentTime =
                TimeSlot(
                    getInt("ir.rd_start").toUInt(),
                    getInt("ir.rd_end").toUInt(),
                ),
            renter =
                User(
                    uid = getInt("u.uid").toUInt(),
                    name = Name(getString("u.name")),
                    email = Email(getString("u.email")),
                    token = getString("u.token").toToken(),
                ),
            court =
                Court(
                    crid = getInt("cr.crid").toUInt(),
                    name = Name(getString("cr.name")),
                    club =
                        Club(
                            cid = getInt("c.cid").toUInt(),
                            name = Name(getString("c.name")),
                            owner =
                                User(
                                    uid = getInt("co.uid").toUInt(),
                                    name = Name(getString("co.name")),
                                    email = Email(getString("co.email")),
                                    token = getString("co.token").toToken(),
                                ),
                        ),
                ),
        )
}
