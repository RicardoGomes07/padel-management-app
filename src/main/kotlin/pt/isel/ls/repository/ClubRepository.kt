package pt.isel.ls.repository

import pt.isel.ls.domain.Club
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.PaginationInfo

/**
 * Generic Interface for a Club repository that supports CRUD operations.
 */
interface ClubRepository : Repository<Club> {
    /**
     * Function that creates a new Club.
     * cid is automatically incremented so it's not received as a parameter to the function.
     */
    fun createClub(
        name: Name,
        ownerId: UInt,
    ): Club

    fun findClubsByName(
        name: Name,
        limit: Int = 10,
        offset: Int = 0,
    ): PaginationInfo<Club>

    /**
     * Function that returns the number of clubs in the system.
     * @return the number of clubs in the system.
     */
    fun count(): Int
}
