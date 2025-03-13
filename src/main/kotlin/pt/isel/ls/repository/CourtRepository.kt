package pt.isel.ls.repository

import pt.isel.ls.domain.Court

/**
 * Generic Interface for a Court repository that supports CRUD operations.
 */
interface CourtRepository : Repository<Court> {
    /**
     * Function that creates a new Court.
     * Returns the created element.
     * crid is automatically incremented so it's not received as a parameter to the function.
     */
    fun createCourt(
        name: String,
        clubId: UInt,
    ): Court
}
