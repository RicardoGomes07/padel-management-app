package pt.isel.ls.services

import pt.isel.ls.domain.Club
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.User
import pt.isel.ls.repository.TransactionManager
import pt.isel.ls.webapi.dto.PaginationInfo

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
    ): Result<List<Club>> =
        runCatching {
            trxManager.run {
                clubRepo.findAll(limit, skip)
            }
        }

    fun numberOfClubs(): Result<PaginationInfo> =
        runCatching {
            trxManager.run {
                PaginationInfo(
                    clubRepo.count(),
                )
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
