package pt.isel.ls.services

import pt.isel.ls.domain.Club
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.User
import pt.isel.ls.repository.ClubRepository

class ClubService(
    private val clubRepo: ClubRepository,
) {
    /**
     * Function that returns all clubs in the system
     * @return list of clubs
     */
    fun getClubs(
        limit: Int,
        skip: Int,
    ): Result<List<Club>> =
        runCatching {
            clubRepo.findAll(limit, skip)
        }

    /**
     * Function that returns a club by its identifier
     * @param cid the club identifier
     * @return either the club or an error indicating that the club was not found
     */
    fun getClubById(cid: UInt): Result<Club> =
        runCatching {
            checkNotNull(clubRepo.findByIdentifier(cid)) { "Club with $cid not found" }
        }

    fun createClub(
        name: Name,
        owner: User,
    ): Result<Club> =
        runCatching {
            require(clubRepo.findClubByName(name) == null) { "Club with name $name already exists" }
            clubRepo.createClub(name, owner.uid)
        }
}
