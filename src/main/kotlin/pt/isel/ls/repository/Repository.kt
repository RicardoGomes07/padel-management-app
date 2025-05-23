package pt.isel.ls.repository

import pt.isel.ls.domain.PaginationInfo

/**
 * Generic Interface for a repository.
 */
interface Repository<T> {
    /**
     * Function that updates an element if it already exists, otherwise creates one.
     */
    fun save(element: T)

    /**
     * Function that finds a specific element.
     * If it's found, returns the element, otherwise returns null.
     */
    fun findByIdentifier(id: UInt): T?

    /**
     * Function that finds every element.
     * Returns a list with the elements (if no element is on the column yet, returns an empty list).
     */
    fun findAll(
        limit: Int = 10,
        offset: Int = 0,
    ): PaginationInfo<T>

    /**
     * Function that deletes an element.
     */
    fun deleteByIdentifier(id: UInt)

    /**
     * Function that deletes every element.
     */
    fun clear()
}
