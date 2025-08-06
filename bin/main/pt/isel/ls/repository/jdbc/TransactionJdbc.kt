@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.repository.*
import java.sql.Connection

class TransactionJdbc(
    connection: Connection,
) : Transaction {
    override val userRepo: UserRepository = UserRepositoryJdbc(connection)
    override val clubRepo: ClubRepository = ClubRepositoryJdbc(connection)
    override val courtRepo: CourtRepository = CourtRepositoryJdbc(connection)
    override val rentalRepo: RentalRepository = RentalRepositoryJdbc(connection)
}
