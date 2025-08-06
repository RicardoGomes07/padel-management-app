@file:Suppress("ktlint:standard:filename", "ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.mem

import pt.isel.ls.repository.*

class TransactionInMem : Transaction {
    override val userRepo: UserRepository = UserRepositoryInMem
    override val clubRepo: ClubRepository = ClubRepositoryInMem
    override val courtRepo: CourtRepository = CourtRepositoryInMem
    override val rentalRepo: RentalRepository = RentalRepositoryInMem
}
