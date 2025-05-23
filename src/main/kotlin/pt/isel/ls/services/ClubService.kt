package pt.isel.ls.services

import pt.isel.ls.domain.Club
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.PaginationInfo
import pt.isel.ls.domain.User
import pt.isel.ls.repository.TransactionManager

class ClubService(
    private val trxManager: TransactionManager,
) {
    /**
     * Function that returns all clubs in the system
     * @return list of clubs
     */
    fun getClubs(
        limit: Int,
        skip: Int,
        name: Name? = null,
    ): Result<PaginationInfo<Club>> =
        runCatching {
            trxManager.run {
                name?.let {
                    clubRepo.findClubsByName(name, limit, skip)
                } ?: clubRepo.findAll(limit, skip)
            }
        }

    /**
     * Function that returns a club by its identifier
     * @param cid the club identifier
     * @return either the club or an error indicating that the club was not found
     */
    fun getClubById(cid: UInt): Result<Club> =
        runCatching {
            trxManager.run {
                clubRepo.findByIdentifier(cid)
                    ?: throw ClubError.ClubNotFound(cid)
            }
        }

    /**
     * Function that creates a new club
     * @param name the name of the club
     * @param owner the owner of the club
     * @return either the created club or an error indicating that the club could not be created
     */
    fun createClub(
        name: Name,
        owner: User,
    ): Result<Club> =
        runCatching {
            trxManager.run {
                clubRepo.createClub(name, owner.uid)
            }
        }
}
