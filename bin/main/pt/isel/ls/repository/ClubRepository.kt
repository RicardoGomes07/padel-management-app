package pt.isel.ls.repository

import pt.isel.ls.domain.Club
import pt.isel.ls.domain.Name

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

    /**
     * Function that search for a Club by a given name.
     * @param name the name of the Club to search for.
     * @return the Club with the given name or null if it doesn't exist.
     */
    fun findClubByName(name: Name): Club?

    /**
     * Function that returns the number of clubs in the system.
     * @return the number of clubs in the system.
     */
    fun count(): Int
}
