package pt.isel.ls.services

import pt.isel.ls.domain.Club
import pt.isel.ls.domain.User
import pt.isel.ls.repository.ClubRepository

sealed class ClubError {
    data object ClubNotFound : ClubError()

    data class ClubAlreadyExists(
        val clubName: String,
    ) : ClubError()
}

class ClubService(
    private val clubRepo: ClubRepository,
) {
    fun getClubs(): List<Club> = clubRepo.findAll()

    fun getClubById(cid: UInt): Either<ClubError.ClubNotFound, Club> {
        val club = clubRepo.findByIdentifier(cid) ?: return failure(ClubError.ClubNotFound)
        return success(club)
    }

    fun createClub(
        name: String,
        owner: User,
    ): Either<ClubError.ClubAlreadyExists, Club> {
        if(clubRepo.findClubByName(name) != null)
            return failure(ClubError.ClubAlreadyExists(name))
        val club = clubRepo.createClub(name, owner.uid)
        return success(club)
    }
}
