package pt.isel.ls.repository

interface Transaction {
    val clubRepo: ClubRepository
    val userRepo: UserRepository
    val courtRepo: CourtRepository
    val rentalRepo: RentalRepository
}
