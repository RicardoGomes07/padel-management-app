package pt.isel.ls.repository

import pt.isel.ls.domain.Court
import pt.isel.ls.domain.Name

/**
 * Generic Interface for a Court repository that supports CRUD operations.
 */
interface CourtRepository : Repository<Court> {
    /**
     * Function that creates a new Court.
     * crid is automatically incremented so it's not received as a parameter to the function.
     */
    fun createCourt(
        name: Name,
        clubId: UInt,
    )
}
