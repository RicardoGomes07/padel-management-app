@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import pt.isel.ls.repository.*
import javax.sql.DataSource

class TransactionJdbc(
    dataSource: DataSource,
) : Transaction {
    override val userRepo: UserRepository = UserRepositoryJdbc(DataSource)
    override val clubRepo: ClubRepository = ClubRepositoryJdbc(DataSource)
    override val courtRepo: CourtRepository = CourtRepositoryJdbc(DataSource)
    override val rentalRepo: RentalRepository = RentalRepositoryJdbc(DataSource)
}
