package pt.isel.ls.repository

import pt.isel.ls.domain.Club

/**
 * Generic Interface for a Club repository that supports CRUD operations.
 */
interface ClubRepository : Repository<Club> {
    /**
     * Function that creates a new Club.
     * cid is automatically incremented so it's not received as a parameter to the function.
     */
    fun createClub(
        name: String,
        ownerId: UInt,
    )

    fun findClubByName(name: String): Club?
}
